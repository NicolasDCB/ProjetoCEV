# CEV - Sistema de Controle de Vetoração de Emergência

Este é um sistema crítico de suporte à decisão para controladores de tráfego aéreo (**ATC**), projetado para gerenciar incidentes de emergência aeronáutica e coordenar rotas alternativas de pouso na região sudeste do Brasil (e suporte internacional). O projeto adota a arquitetura **MVC (Model-View-Controller)** e integra banco de dados embarcado, leitura de dados geográficos locais e consumo de APIs externas de aviação civil em tempo real.

---

##  Funcionalidades e Casos de Uso (UC)

###  UC-01: Registrar Incidente de Emergência
* **Declaração de Alertas:** Permite ao controlador registrar um incidente selecionando o nível de criticidade aeronáutica (`URGENCIA`, `PERIGO`, `ANGUSTIA`).
* **Validação de Fronteiras Geográficas:** O sistema analisa a coordenada fornecida (ex: `23S, 45W`). Se os dados estiverem fora do espaço aéreo monitorado do Brasil (ex: `55, 55`), uma lógica interativa de segurança intercepta a ação através de um **Alerta de Território**, solicitando confirmação para o processamento internacional.

### UC-02: Analisar Alternativas de Pouso
* **Geolocalização Inteligente (Fórmula de Haversine):** O sistema varre uma base de dados offline local contendo milhares de aeródromos mundiais, filtrando apenas pistas operacionais de médio e grande porte. Calcula matematicamente a distância real em quilômetros em relação ao ponto da emergência.
* **Ordenação Regulamentar (< 5 segundos):** Retorna o Top 3 aeroportos mais próximos priorizando o critério de **Status de Viabilidade** no topo da tabela e, em caso de empate, ordena pelo **menor tempo estimado de voo**. Todo o processamento (leitura de arquivos + chamadas de rede) é otimizado para responder de forma imediata à interface.

### UC-03: Consultar Histórico e Auditoria
* **Persistência de Dados (Banco H2):** Todos os incidentes, carimbos de data/hora e logs operacionais são persistidos localmente.
* **Controle de Acesso por Perfil:** A aba de relatórios e auditoria é de uso restrito a perfis de **Chefia/Analista**, bloqueando o acesso de usuários controladores padrão para preservar a integridade dos dados históricos.

### UC-04: Atualizar / Recalcular Rotas
* **Modificação em Tempo Real:** Permite alterar o status e os parâmetros de uma emergência ativa diretamente na tela operacional. O sistema limpa a tabela gráfica, refaz as chamadas de API e recalcula a ordenação das rotas dinamicamente para o controlador.

### UC-05: Autenticação Segura e Resiliência
* **Controle Corporativo:** Separação estrita de privilégios em tela entre os usuários `ATC` e `ANALISTA`.
* **Proteção Contra Força Bruta:** Sistema interno de segurança que bloqueia a conta do usuário na memória após **3 tentativas incorretas** consecutivas de senha, exigindo a reinicialização da aplicação.

---

## Integração com APIs Externas

O sistema opera com um modelo de comunicação híbrido. Para os aeródromos selecionados pelo algoritmo de proximidade local, são disparadas requisições HTTP paralelas em tempo real:
1.  **AirportDB:** Autenticação via chave/token em cabeçalho HTTP para extração do nome oficial e dados estruturados da pista contidos no payload JSON.
2.  **AviationWeather:** Consulta e monitoramento das condições meteorológicas locais (dados de teto, visibilidade e string do METAR).
3.  **OpenSky Network:** Rastreamento do tráfego aéreo e densidade de radar circundante à região da coordenada geográfica.

*Nota de Resiliência:* Caso ocorra indisponibilidade de internet ou falha de autenticação do token, o sistema aciona automaticamente o **Modo de Segurança (Fallback)**, utilizando os dados cadastrais offline do arquivo `airports.csv` para garantir a continuidade da operação.

---

## Tecnologias Utilizadas

* **Linguagem:** Java (JDK 17+)
* **Interface Gráfica:** Java Swing (Arquitetura Visual Desconectada)
* **Arquitetura:** MVC (Model-View-Controller)
* **Banco de Dados:** H2 Database Engine (Modo Embarcado / Embedded)
* **Algoritmos:** Fórmula de Haversine para cálculo de distâncias geográficas em superfícies esféricas.
* **Base de Dados Local:** `airports.csv` (Dicionário Global de Infraestrutura Aeroportuária)

---

## Como Configurar e Executar

1.  **Configuração da Base de Dados:** Certifique-se de que o arquivo `airports.csv` está localizado exatamente na **raiz do projeto** (fora da pasta `/src`).
2.  **Injeção do Token da API:** Para visualizar os nomes reais integrados via internet, adicione o seu token do *AirportDB* nas variáveis de ambiente do seu ambiente de execução (IntelliJ / Eclipse):
    * **Nome da Variável:** `AIRPORT_DB_TOKEN`
3.  **Execução:** Execute o projeto a partir da classe principal de login: `service.LoginView.java`.
4.  **Credenciais Padrão para Testes:**
    * **Operador ATC:** Login: `atc1` \| Senha: `1234`
    * **Analista de Auditoria:** Login: `analista1` \| Senha: `1234`