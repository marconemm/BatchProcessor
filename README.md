# BatchProcessor 🚀

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-blue?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

Uma solução desktop robusta desenvolvida para automatizar a análise mensal de grandes volumes de dados. O *
*BatchProcessor** elimina o trabalho manual de abrir, ler e formatar dados de dezenas de planilhas, transformando-os em
inputs prontos para sistemas corporativos.

## 📋 Sobre o Projeto

O projeto nasceu da necessidade de otimizar a análise de mais de 60 planilhas mensais. A ferramenta realiza o parsing de
abas específicas, aplica regras de formatação e gera um arquivo consolidado em `.txt` para integração em lote (*batch
input*), garantindo integridade total dos dados e eliminando o erro humano.

### Principais Funcionalidades:

* **Processamento em Lote:** Seleção múltipla de arquivos para análise simultânea.
* **Interface Moderna:** GUI desenvolvida em JavaFX com foco em UX, utilizando uma paleta de cores tecnológica (*Dark
  Mode*).
* **Logs em Tempo Real:** Visualização dinâmica de processos através de um sistema de logs assíncronos integrados à
  interface.
* **Exportação Otimizada:** Geração de arquivos de saída formatados para integração direta em sistemas de destino.

## 🛠️ Stack Técnica

* **Linguagem:** Java 21 (LTS)
* **Interface Gráfica:** JavaFX (FXML)
* **Logs:** Log4j2 com **LMAX Disruptor** (Processamento Assíncrono)
* **Gerenciamento de Build:** Gradle 8.x
* **Manipulação de Excel:** Apache POI (poi-ooxml)

## 🚀 Como Executar

### 💻 Para Usuários Windows

1. Vá até a seção **[Releases](https://github.com/seu-usuario/BatchProcessor/releases)**.
2. Baixe o arquivo `BatchProcessor.exe`.
3. Certifique-se de ter o **Java 21** instalado.
4. Execute o programa e selecione os arquivos para processamento.

### 🐧 Para Usuários Linux

Para rodar a versão portátil no Linux com suporte total a logs assíncronos e acesso nativo ao JavaFX, utilize o comando:

```bash
java -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector \
     --enable-native-access=javafx.graphics \
     -jar BatchProcessor_stable.jar
```

### 🛠️ No linux, caso queira criar um atalho:

Siga os passos abaixo para iniciar o BatchProcessor apenas digitando **\<seu_atalho\>** no terminal:

- Abrir o arquivo de aliases:
  No terminal, execute o comando para abrir o editor:

```Bash
nano ~/.bash_aliases
```

- Adicionar o comando:
  Copie a linha abaixo e cole no final do arquivo (ajuste o caminho /home/usuario/... para o local real do seu arquivo
  .jar):

```Bash
alias seu_atalho='java -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector --enable-native-access=javafx.graphics -jar ~/</caminho/completo/para>/BatchProcessor.jar'
```

- Salve e saia do editor:
    - Pressione Ctrl + O e depois Enter, para salvar.
    - Pressione Ctrl + X, para fechar o editor.
- Atualizar o terminal, executando o comando para aplicar as mudanças imediatamente:

```Bash
source ~/.bashrc
```

### Usar:

Agora você pode abrir o programa de qualquer lugar apenas digitando no seu terminal:

```Bash
seu_atalho
```

### 👨‍💻 Para Desenvolvedores (Build Manual)

- Clone o repositório:

```Bash
git clone [https://github.com/marconemm/BatchProcessor.git](https://github.com/marconemm/BatchProcessor.git)
cd BatchProcessor
```

- Gere o executável otimizado (Fat JAR):

```Bash
./gradlew clean fatJar
```

- O arquivo será gerado em build/libs/BatchProcessor-1.0-executable.jar.

### ⚡ Performance

O BatchProcessor utiliza Log4j2 assíncrono, garantindo que a interface permaneça 100% responsiva enquanto o motor de
análise processa milhares de linhas de Excel em segundo plano.

### 📄 Licença

Distribuído sob a licença MIT. Veja o arquivo LICENSE para mais informações.
___
_Desenvolvido por **[Marcone M. Mendonça](https://github.com/marconemm)** - 2026._