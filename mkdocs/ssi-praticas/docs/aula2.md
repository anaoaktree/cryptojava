# Exercício 2

## Downloads
* [Exercício 2 no GitHub](https://github.com/anapmc/SSI1415/tree/master/Aula1).
* [Projeto completo em zip](https://github.com/anapmc/SSI1415/archive/master.zip).
* [Projeto no GitHub](https://github.com/anapmc/SSI1415/).

## Descrição

## Instruções
1. Correr o servidor com `./server` (pode ser preciso executar *chmod 777 server*);
2. Correr o cliente com `./client`;

- O cliente abre uma interface de escrita. Tudo o que é escrito é impresso no servidor com o id do cliente.
- O servidor fica à escuta de mensagens dos clientes, que imprime no ecrã.

O cliente foi atualizado para enviar mensagens (byte a byte) cifradas ao servidor, que as decifra. 
####Para escolher uma das várias cifras e modos:
1. Correr normalmente o servidor (ver acima);
-  **RC4**: Correr o cliente com `./client` ou `./client rc4`;
-  **AES/CBC/NoPadding**: Correr o cliente com `./client cbc`;
- **AES/CBC/PKCS5Padding**: Correr o cliente com  `./client cbc_pdd`;
-  **AES/CFB8/NoPadding**: Correr o cliente com `./client cfb8`;
-  **AES/CFB8/PKCS5Padding**: Correr o cliente com `./client cbf8_pdd`;
-  **AES/CFB/NoPadding**: Correr o cliente com `./client cfb`;


## Estrutura

    mkdocs.yml    # The configuration file.
    docs/
        index.md  # The documentation homepage.
        ...       # Other markdown pages, images and other files.
