# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 38 - Campus Alameda

Ana Galvão - 75312 - agalvao12@hotmail.com

Filipa Costa - 75888 - filipa.rscosta@gmail.com

Luís Santos - 75964 - lmhbs1@gmail.com



Repositório:
[tecnico-distsys/A_38-project](https://github.com/tecnico-distsys/A_38-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo

Windows


[1] Iniciar servidores de apoio

JUDDI:

```
$CATALINA_HOME/bin/startup.bat
```


[2] Criar pasta temporária

```
cd /tmp/p38
mkdir /tmp/p38 
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone --branch SD_R2 https://github.com/tecnico-distsys/A_38-project.git
```


[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

```
cd ...
mvn clean install
```


-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor CA**

```
cd /tmp/p38/A_38-project/ 
cd ./../ca-ws
mvn clean package
mvn clean install
mvn exec:java
```



[2] Construir e executar **servidor**

```
cd /tmp/p38/A_38-project/ 
cd ./../transporter-ws
mvn clean package
mvn clean install
mvn -Dws.i=2 exec:java  (repetir para i=1 , servidor impar)
```

[3] Construir **cliente** e executar testes

```
cd /tmp/p38/A_38-project/ 
cd ./../transporter-ws-cli
mvn clean package
mvn clean install
```


[4] Construir **cliente CA** e executar testes

```
cd /tmp/p38/A_38-project/ 
cd ./../ca-ws-cli
mvn clean package
mvn clean install
```

[5] Correr testes fornecidos pelo docente ao TRANSPORTER

```
cd /tmp/p38/A_38-project/ 
cd ./../ca-ws-cli_eval
mvn clean package
mvn clean install
mvn verify
```

...


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor Secundario**

```
cd /tmp/p38/A_38-project/ 
cd ./../broker-ws
mvn clean package
mvn clean install
mvn -Dws.i=2 exec:java 
```

[2] Construir e executar **servidor Primario **

```
aguardar ate ser impresso, no secundario, o aviso de que o mesmo esta pronto
cd /tmp/p38/A_38-project/ 
cd ./../broker-ws
mvn clean package
mvn clean install
mvn exec:java 
```

[3] Construir e executar testes ao Broker fornecidos pelo docente

```
cd /tmp/p38/A_38-project/ 
cd ./../broker-ws-cli_eval
mvn clean package
mvn clean install
mvn verify
```




[2] Construir **cliente** e executar testes (inclui shell para testar "manualmente")

```
cd /tmp/p38/A_38-project/ 
cd ./../broker-ws-cli
mvn clean package
mvn clean install
mvn exec:java
```

...

-------------------------------------------------------------------------------
**FIM**
