# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 38 - Campus Alameda
*(preencher com número do grupo de SD no Fénix e depois apagar esta linha)*

... ... ...Ana Galvão - 75312 - 

... ... ...Filipa Costa - 75888 - filipa.rscosta@gmail.com

... ... ...Luís Santos - 75964 - lmhbs1@gmail.com



Repositório:
[tecnico-distsys/A_38-project](https://github.com/tecnico-distsys/A_38-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo

Indicar Windows


[1] Iniciar servidores de apoio

JUDDI:
```
...
```


[2] Criar pasta temporária

```
cd ...
mkdir ...
```


[3] Obter código fonte do projeto (versão entregue)

```
git clone ... 
```
*(colocar aqui comandos git para obter a versão entregue a partir da tag e depois apagar esta linha)*


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

[1] Construir e executar **servidor**

```
cd ...-ws
mvn clean install
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd ...-ws-cli
mvn clean install
```

...


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

```
cd ...-ws
mvn clean install
mvn exec:java
```


[2] Construir **cliente** e executar testes

```
cd ...-ws-cli
mvn clean install
```

...

-------------------------------------------------------------------------------
**FIM**
