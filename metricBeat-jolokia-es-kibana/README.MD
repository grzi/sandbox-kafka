## Objectif
Monitorer les métriques kafka en se basant sur la stack suivante :
- Kafka (broker)
- Agent jolokia (agent d'export JMX)
- Metric beat (Appli de récolte des métriques)
- Elastic search (BDD)
- Kibana (Interface)


1. Déployer l'agent jolokia en précisant un port différent pour chaque broker, puis pour chaque worker kafka connect
```
KAFKA_OPTS="$KAFKA_OPTS -javaagent:jolokia-jvm-1.6.0-agent.jar -javaagent:agent.jar=port=8779,host=localhost"
```

2. Lancer ES, Kibana

3. Configurer metric beats & le module jolokia (voir fichiers associés)

4. Lancer metric beat

5. Connexion à Kibana : Ajout d'un index dans management, récupérer celuis de metric beat

6. Discover : Les métriques sont là. Ne reste plus qu'à créer un dashboard

