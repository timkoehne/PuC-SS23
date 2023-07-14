import * as vscode from 'vscode';
import * as net from "net";

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

export function activate(context: vscode.ExtensionContext) {
	context.subscriptions.push(vscode.languages.registerDocumentSemanticTokensProvider({ language: 'semanticLanguage' }, new DocumentSemanticTokensProvider(), legend));
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

	receivedHighlighting = false;
	highlighting: Highlight[] = [];
	output = vscode.window.createOutputChannel("test");
	client = net.createConnection({ port: 3000 }, () => {
		this.output.appendLine('connected to server!');

		this.client.on("data", (answer) => {
			this.highlighting = JSON.parse(answer.toString());
			this.receivedHighlighting = true;
			this.output.appendLine("test");
			this.output.appendLine(answer.toString());
		});

	});

	async provideDocumentSemanticTokens(document: vscode.TextDocument, token: vscode.CancellationToken): Promise<vscode.SemanticTokens> {
		const allTokens = this._parseText(document.getText());
		const builder = new vscode.SemanticTokensBuilder();

		this.client.write(document.getText());
		this.client.end(); //is this really necessary? do we even want this?

		//wait for callback to arrive
		while (!this.receivedHighlighting) {
			await new Promise(f => setTimeout(f, 10));
		}

		//print token type map
		tokenTypes.forEach((value: number, key: string) => {
			console.log(key, value);
			this.output.appendLine(key + ": " + value);
		});

		for (let i = 0; i < this.highlighting.length; i++) {
			builder.push( //lineNum startindex length tokenTypesLegend-index tokenModifiersLegend
				this.highlighting[i]["lineNum"],
				this.highlighting[i]["start"],
				this.highlighting[i]["length"],
				this._encodeTokenType(this.highlighting[i]["name"]),
				0);
			this.output.appendLine("" + this._encodeTokenType(this.highlighting[i]["name"]));
		}


		this.receivedHighlighting = false;
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
