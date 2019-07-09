---dataset-----
1.0 - para rodar o programa é necessário um dataset em formato n-triples (.nt). Recomendo utilizar o site https://old.datahub.io/dataset
1.1 - caso encontre um dataset que não esteja em formato .nt, este pode ser convertido utilizando o arquivo rdf2rdf-1.0.1-2.3.1 que está na pasta bib. Utilize esse site para mais informações http://www.l3s.de/~minack/rdf2rdf/
1.2 - para rodar o programa é só ir na classe TextProcessor e mudar nessa linha: FileInputStream fis = new FileInputStream("datasets/stw.nt"); Eu estava colocando os datasets sempre na pasta dataset, caso não queira é só remover o datasets/ e colocar o dataset na pasta principal do programa.

--Funções do programa--
1 - a Thread threadExtractRdf (não precisava ser uma thread) está convertendo os dados do dataset para um formato chamado Triple, que terá sujeito, predicado e objeto.
2 - Depois o programa fará uma função que verá quantos processadores tem disponível o seu computador e fará n threds (quantidade de processadores) nas quais extrairá as entidades.
3 - O mesmo acontecerá para instâncias
4 -  Após as duas terem acabado, um arquivo com as entidades e instâncias será gerado.
5 - O mesmo que foi feito para entidades e instâncias será feito para relacionamentos.
6 - NewData é feito em ordem de otimização de tempo, pois nela só estarão as triplas que apresentam instâncias no sujeito e no objeto e relacionamentos no predicado.
7 - MinMaxRelationships calcula o mínimo e o máximo das cardinalidades dos relacionamentos e os grava em arquivos.
8 - ExtractAttributes extrai os atributos
9 - MinMaxattributes calcula o mínimo e o máximo das cardinalidades dos atributos e os grava em arquivos.
10 - FileEntitiesAttributes grava em um arquivo o nome das entidades e dos relacionamentos
11 - Coherence é o nível de estruturacao do dataset, aconselho a ler o artigo "apples and oranges: a comparison of rdf benchmarks and real rdf datasets" disponivel em : https://researcher.watson.ibm.com/researcher/files/us-sduan/sigmod2011_RDF_benchmark_duan.pdf

Observacoes:
O.1 - toda vez que o programa rodar ele gerará arquivos e caso vc rode ele novamente, os dados que estão escritos nos arquivos texto permaneceram e os novos dados serão escritos em baixo, portanto ao rodar o programa salve em alguma outra pasta os arquivos gerados ou os delete.
O.2 - O programa deve gerar 6 arquivos texto : Entities&Attributes, Entities&Instances, MinMaxAttributes, MinMaxRelationships, Relationships, StatiscialData. Caso falte algum arquivo, é porque não foi possível extrair alguma dessas informações. Geralmente é relacionado aos relacionamentos, portanto o dataset não apresenta relacionamentos.
0.3 - Datasets com mais de 2 milhões de triplas podem levar dias para rodar. Berlin dataset com 1000 produtos e 374911 triplas demorou cerca de 10 minutos.

--EXPLICANDO TODAS AS FUNÇÕES PASSO A PASSO
1 - ExtractEntites
	para ser entidade é necessário que o predicado tenha esse formato - http://www.w3.org/1999/02/22-rdf-syntax-ns#type, então o sujeito será a entidade e o objeto será a instância. Então é só verificar se a instância já não foi colocada.

2 - ExtractInstances
	para ser entidade é necessário que o predicado tenha esse formato - http://www.w3.org/1999/02/22-rdf-syntax-ns#type, então o sujeito será a entidade e o objeto será a instância. 
	Agora é necessário percorrer a lista de entidades e verificar quando alguma delas é igual ao sujeito da tripla. 
	Depois verificar se ela ainda não foi colocada na lista de instâncias da entidade.

3 - FileEntitiesInstances
	grava em um arquivo as entidades e instâncias

4 - ExtractRelationships
	Verifica primeiro se na tripla o sujeito e o predicado são instâncias de alguma entidade. Depois faz algumas comparações
	Verifica se o predicado (nome do relacionamento) já foi inserido na lista de relacionamentos, se não, insere. Caso já tenha sido 	inserido, retorna

5 -  NewDataWithInstancesAndRelationships
	Para otimizar o tempo da função que vai calcular o min e max dos relacionamentos, foi criado uma nova lista de triplas onde só 		terão triplas que apresentam o sujeito e o objeto como instâncias de alguma entidade e o predicado como sendo algum relacionamento.
	É então colocado a tripla na lista de triplas chamada newData

6 - minMaxRelationships
	essa função é um pouco mais complicada de entender. Para cada relacionamento r, eu vou pegar uma instância de cada entidade e vou percorrer o newData verificando se o predicado é igual a r, e se o sujeito da tripla é igual a instância da entidade. Se sim, adiciono em uma nova lista de triplas chamada smallData.
	Essa parte foi feita pq estou calculando o min e max de cada relacionamento. Eh um min e um max de relacionamento por vez, ou seja, vou pegar o relacionamento "X" e vou ver todos os relaciomentos dele, e para isso preciso salvar em algum lugar essas informações, então todos do smallData vão ter o relacionamento X. Fiz isso para não ter que percorrer todo o extractData (lista onde estão todos os dados)
	Vale ressaltar então que o newData tem as triplas onde o r (relacionamento da vez) está presente no predicado e o sujeito (instância) está presente na entidade da vez. Ou seja, eu 	tenho uma lista 	apenas 	com esse relacionamento r, e onde o sujeito é instância da entidade que eu estou utilizando 		agora
	Com essa lista newData eu vou percorrer toda a lista de entidades e para cada entidade eu vou ter uma lista de instâncias que ja foram contadas (instancesAlreadyCounted) para o sujeito e para o 		objeto (instancesAlreadyCounted2) e tbm a quantidade de vezes que determinada instância apareceu no relacionamento
	(quantityOfOcorrence) para o sujeito e (quantityOfOcorrence2) para o objeto.
	String entityAnalyzed é igual a entidade do primeiro laço for e String entityCompared = entidade do segundo laço for.
	Para o sujeito : percorrendo o smallData verificando se o objeto (instância) dessa tripla do smallData pertence a  entidade entityCompared, se sim, validacao = 1 e vou acrescentar um na posição do vetor quantityOfOcorrence que a instância (sujeito) está na lista de instâncias da entidade (entityAnalyzed)
	Verificar se ela ainda não foi contabilizada, se nao foi, então adicionar na lista 	instancesAlreadyCounted.
	Para o predicado : encontrar a posição onde o objeto da tripla (instância) se encontra no vetor de instâncias da entityCompared.
	Acrescentar um nessa posição no vetor quantityOfOcorrence2
	Verificar se ela ainda nao foi contabilizada, se nao foi, então adicionar na lista 	instancesAlreadyCounted2.
	Caso validacao = 1,  a cardinalidade máxima do relacionamento do sujeito vai ser igual ao maior valor do vetor quantityOfOcorrence e o mínimo será a divisão entre a quantidade de instâncias que foram contabilizadas sobre a quantidade de instâncias da entidade.
	o mesmo serve para a cardinalidade máxima do relacionamento do objeto.
	então é somado 2 a quantidade de relacionamentos (pq um relacionamento tem 2 participantes) e se o minimo de algum deles foi menor que 1, é entao acrescentado na variavel cardAbaixoDe1, que serve como dado estatístico.
	´É tirado a media do min e do max e validação volta a ser 0.

7 - extractAttributes
	Para cada entidade, percorrendo cada instância. Para cada tripla do dataset se o objeto não começar com "http://" e se o sujeito da tripla do dataset for igual a instância da vez, então o 		predicado é possivelmente um atributo. Verificar se ele já foi colocado, se nao, colocar ele numa lista e depois de percorrer todos as instâncias, colocar essa lista na lista de atributos da 		entidade.

8 - minMaxAttribute
	percorrendo as entidades, se o quantidade de atributos dela for diferente de 0, então percorrer as triplas do dataset, se o predicado for igual ao atributo da vez e se o sujeito for igual a alguma instância da entidade, é só achar a posição dela na lista de entidades, incrementar 1 na quantityOfOcorrence e marcar na instancesAlreadyCounted.
	O min e max como o minMaxRelationships.
	
9 - Coherence
	ler o artigo apples and oranges pra entender o que é coherence
	denominadorWt será usado para o cálculo wt (weighted sum of the coverage)
	Percorrendo as entidades, para cada atributo: vou ter uma lista de instances, percorrendo todos as triplas do dataset: se o predicado for igual ao atributo da vez, devo verificar se o sujeito (instância) pertence a entidade da vez. Se sim, adicionar ela na lista de instances e somar um no occurrences.
	Occurrences é basicamente: pegando uma instância i de uma entidade e, vou verificar quantos atributos essa instância i tem. Esse valor vai ser somado com os de todas as instâncias da entidade, chamaremos o valor total de num. Occurrences vai ser igual a num / (quantidade de atributos da entidade x quantidade de instâncias da entidade)
	Coherence é o somatório para cada entidade de wt * coverage. 
	wt = (quantidade de instancias da entidade + quantidade de atributos) / denominadorWt
	coverage = occurrences / (quantidade de instâncias da entidade * quantidade de atributos da entidade)


Qualquer dúvida entre em contato pelo email : lucascobucci@hotmail.com
