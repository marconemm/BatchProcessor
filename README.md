# BatchProcessor 🚀

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-blue?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

Uma solução desktop robusta desenvolvida para automatizar a análise mensal de grandes volumes de dados. O **ExcelAnalyzer Pro** elimina o trabalho manual de abrir, ler e formatar dados de dezenas de planilhas, transformando-os em inputs prontos para sistemas corporativos.

## 📋 Sobre o Projeto
O projeto nasceu da necessidade de otimizar a análise de mais de 60 planilhas mensais. A ferramenta realiza o parsing de abas específicas, aplica regras de formatação e gera um arquivo consolidado em `.txt` para integração em lote (batch input), garantindo integridade total dos dados e eliminando o erro humano.

### Principais Funcionalidades:
* **Processamento em Lote:** Seleção múltipla de arquivos para análise simultânea.
* **Interface Moderna:** GUI desenvolvida em JavaFX com foco em UX, utilizando uma paleta de cores tecnológica (*Dark Mode*).
* **Logs em Tempo Real:** Visualização dinâmica de sucessos e erros através de um sistema de scroll interativo baseado em `VBox` e `ScrollPane`.
* **Exportação Otimizada:** Geração de arquivos de saída formatados (Strings) para integração direta em sistemas de destino.

## 🛠️ Stack Técnica
* **Linguagem:** Java
* **Interface Gráfica:** JavaFX (FXML)
  * _Layout construído com JavaFX Scene Builder e estilizado com CSS3_
* **Gerenciamento de Build:** Gradle
* **Manipulação de Excel:** Apache POI (poi-ooxml)

## 🚀 Como Executar
### Pré-requisitos
* JDK 17+
* Gradle (ou utilizar o Gradle Wrapper incluso no projeto).

### Instalação e Execução
1. Clone o repos