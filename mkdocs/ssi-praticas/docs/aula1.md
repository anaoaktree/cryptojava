# Exercício 1

## Downloads
* [Exercício 1 no GitHub](https://github.com/anapmc/SSI1415/tree/master/Aula1).
* [Projeto completo em zip](https://github.com/anapmc/SSI1415/archive/master.zip).
* [Projeto no GitHub](https://github.com/anapmc/SSI1415/).

## Descrição

## Cifra de ficheiro (utilizando a cifra RC4 da JCA/JCE)

- **Pré-requisitos**: ter um ficheiro *texto_limpo* (sem extensões) na diretoria.
- **Gerar chave**: escrever uma chave no ficheiro *chave* ou correr `./gerar_chave`;
- **Cifrar**: correr `./cifrar_rc4` utiliza os ficheiros *chave* e *texto_limpo* e cifra para o ficheiro *criptograma*;
- **Decifrar**: correr `./decifrar_rc4` utiliza os ficheiros *chave* e *criptograma* e decifra para o ficheiro *texto_decifrado*;

#### Alternativa:
- `java Rc4 -genkey chave`
- `java Rc4 -enc chave texto_limpo criptograma`
- `java Rc4 -dec chave criptograma texto_decifrado`


### Implementação própria da cifra RC4 (Arc4.java)
Em desenvolvimento


## Estrutura

    mkdocs.yml    # The configuration file.
    docs/
        index.md  # The documentation homepage.
        ...       # Other markdown pages, images and other files.
