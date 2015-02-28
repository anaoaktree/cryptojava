# SSI1415
Trabalhos práticos da UC de SSI 

#### Por Ana Paula Carvalho e Fábio Fernandes

##client-server
1. Correr o servidor com *./server* (pode ser preciso executar "chmod 777 server");
2. Correr o cliente com *./client*;

- O cliente abre uma interface de escrita. Tudo o que é escrito é impresso no servidor com o id do cliente.
- O servidor fica à escuta de mensagens dos clientes, que imprime no ecrã.

###Atualização Aula 2

O cliente foi atualizado para enviar mensagens cifradas ao servidor, que as decifra. 
####Para escolher uma das várias cifras e modos:
1. Correr normalmente o servidor (ver acima);
-  **RC4**: Correr o cliente com *./client* ou *./client rc4*;
-  **AES/CBC/NoPadding**: Correr o cliente com *./client cbc*;
- **AES/CBC/PKCS5Padding**: Correr o cliente com  *./client cbc_pdd*;
-  **AES/CFB8/NoPadding**: Correr o cliente com *./client cfb8*;
-  **AES/CFB8/PKCS5Padding**: Correr o cliente com *./client cbf8_pdd*;
-  **AES/CFB/NoPadding**: Correr o cliente com *./client cfb*;


##rc4

### Implementação JCA/JCE (Rc4.java)
- **Pré-requisitos**: ter um ficheiro *texto_limpo* (sem extensões) na diretoria.
- **Gerar chave**: escrever uma chave no ficheiro *chave* ou correr *./gerar_chave*;
- **Cifrar**: correr *./cifrar_rc4* utiliza os ficheiros *chave* e *texto_limpo* e cifra para o ficheiro *criptograma*;
- **Decifrar**: correr *./decifrar_rc4* utiliza os ficheiros *chave* e *criptograma* e decifra para o ficheiro *texto_decifrado*;

####Alternativa:
- **java Rc4 -genkey** *ficheiro_chave*
- **java Rc4 -enc** *ficheiro_chave* *ficheiro_texto_limpo* *ficheiro_criptograma*
- **java Rc4 -dec** *ficheiro_chave* *ficheiro_criptograma* *ficheiro_texto_decifrado*


### Implementação própria (Arc4.java)
Em desenvolvimento
