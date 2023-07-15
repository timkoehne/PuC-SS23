import * as vscode from 'vscode';
import * as net from "net";
import { exec } from 'child_process';

const tokenTypes = new Map<string, number>();
const tokenModifiers = new Map<string, number>();

const legend = (function () {
	const tokenTypesLegend = [
		'comment', 'string', 'keyword', 'number', 'regexp', 'operator', 'namespace',
		'type', 'struct', 'class', 'interface', 'enum', 'typeParameter', 'function',
		'method', 'decorator', 'macro', 'variable', 'parameter', 'property', 'label'
	];
	tokenTypesLegend.forEach((tokenType, index) => tokenTypes.set(tokenType, index));

	const tokenModifiersLegend = [
		'declaration', 'documentation', 'readonly', 'static', 'abstract', 'deprecated',
		'modification', 'async'
	];
	tokenModifiersLegend.forEach((tokenModifier, index) => tokenModifiers.set(tokenModifier, index));

	return new vscode.SemanticTokensLegend(tokenTypesLegend, tokenModifiersLegend);
})();

const output123 = vscode.window.createOutputChannel("output123");

//TODO laufenden gradle prozess beenden bei deaktivieren

export async function activate(context: vscode.ExtensionContext) {
	// exec('gradle build -p ../PuC-SS23/compiler/', (err, output) => {
	// 	if (err) {
	// 		console.error("Compiler could not be built: ", err);
	// 		return;
	// 	}
	// 	console.log("Output: \n", output);
	// });

	exec("gradle run -p " + __dirname + "/../../PuC-SS23/compiler/", (err, output) => {
		if (err) {
			output123.appendLine("Compiler could not be run: " + err);
			return;
		}
		output123.appendLine("Output: \n" + output);
	});

	//TODO evtl warten bis server läuft
	const semanticProvider = new DocumentSemanticTokensProvider();
	context.subscriptions.push(vscode.languages.registerDocumentSemanticTokensProvider({ language: 'semanticLanguage' }, semanticProvider, legend));

}

interface IParsedToken {
	line: number;
	startCharacter: number;
	length: number;
	tokenType: string;
	tokenModifiers: string[];
}

interface Highlight {
	name: string;
	start: number;
	end: number;
	length: number;
	lineNum: number;
}

class DocumentSemanticTokensProvider implements vscode.DocumentSemanticTokensProvider {

	async provideDocumentSemanticTokens(document: vscode.TextDocument): Promise<vscode.SemanticTokens> {
		const allTokens = this._parseText(document.getText());
		const builder = new vscode.SemanticTokensBuilder();

		let receivedHighlighting = false;
		let highlighting: Highlight[] = [];
		const output = vscode.window.createOutputChannel("test");
	
		const client = net.createConnection({ port: 3000 }, () => {
			output.appendLine('connected to server!');
	
			client.on("data", (answer) => {
				highlighting = JSON.parse(answer.toString());
				receivedHighlighting = true;
				output.appendLine("test");
				output.appendLine(answer.toString());
			});
		});

		client.write(document.getText());
		client.end();

		//wait for callback to arrive
		while (!receivedHighlighting) {
			await new Promise(f => setTimeout(f, 10));
		}

		//brauchen wir hier nicht die absoluten werte sondern den abstand zum letzten token?
		for (let i = 0; i < highlighting.length; i++) {
			builder.push( //lineNum startindex length tokenTypesLegend-index tokenModifiersLegend
				highlighting[i]["lineNum"],
				highlighting[i]["start"],
				highlighting[i]["length"],
				this._encodeTokenType(highlighting[i]["name"]),
				0);
			output.appendLine("" + this._encodeTokenType(highlighting[i]["name"]));
		}


		receivedHighlighting = false;
		return builder.build();
	}

	private _encodeTokenType(tokenType: string): number {
		if (tokenTypes.has(tokenType)) {
			return tokenTypes.get(tokenType)!;
		} else if (tokenType === 'notInLegend') {
			return tokenTypes.size + 2;
		}
		return 0;
	}

	private _encodeTokenModifiers(strTokenModifiers: string[]): number {
		let result = 0;
		for (let i = 0; i < strTokenModifiers.length; i++) {
			const tokenModifier = strTokenModifiers[i];
			if (tokenModifiers.has(tokenModifier)) {
				result = result | (1 << tokenModifiers.get(tokenModifier)!);
			} else if (tokenModifier === 'notInLegend') {
				result = result | (1 << tokenModifiers.size + 2);
			}
		}
		return result;
	}

	private _parseText(text: string): IParsedToken[] {
		const r: IParsedToken[] = [];
		const lines = text.split(/\r\n|\r|\n/);
		for (let i = 0; i < lines.length; i++) {
			const line = lines[i];
			let currentOffset = 0;
			do {
				const openOffset = line.indexOf('[', currentOffset);
				if (openOffset === -1) {
					break;
				}
				const closeOffset = line.indexOf(']', openOffset);
				if (closeOffset === -1) {
					break;
				}
				const tokenData = this._parseTextToken(line.substring(openOffset + 1, closeOffset));
				r.push({
					line: i,
					startCharacter: openOffset + 1,
					length: closeOffset - openOffset - 1,
					tokenType: tokenData.tokenType,
					tokenModifiers: tokenData.tokenModifiers
				});
				currentOffset = closeOffset;
			} while (true);
		}
		return r;
	}

	private _parseTextToken(text: string): { tokenType: string; tokenModifiers: string[]; } {
		const parts = text.split('.');
		return {
			tokenType: parts[0],
			tokenModifiers: parts.slice(1)
		};
	}
}
