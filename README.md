Semantic Highlighting for PuC-Lang
===

Supervisor: __[Christoph Hegemann](https://github.com/kritzcreek)__\
Elaborators: __Fabian Jülich, Tim Köhne__

This project is about the implementation of syntax and semantic highlighting for the programming language developed in the "Programming Languages ​​and Compiler Design" module.
For this purpose we created a VS Code extension that communicates with the compiler's Antlr listener and highlights the received tokens in the editor.

See our __[slides](./PuC%20Highlighting%20(VS-Code%20extension).pptx)__ for more information.

## ToDo:
- [ ] convert formatting to semantic token builder entries in extension.ts
- [ ] add remaining formatting rules in listener.kt
- [ ] unify line endings for platform independence