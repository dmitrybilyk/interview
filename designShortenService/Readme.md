docker-compose down -v

docker-compose up --build -d

dmytro@dmytro-N53SN:~/dev/projects/interview/jmetter/apache-jmeter-5.6.3/bin$ sh jmeter.sh

grafana dashboard - 4701



I have shortener service implemented. I have generator and redirector. I have separate service for spring cloud and separate for eureka. I also have postges db used and zookeeper to coordinate generation of short url - so that it's aligned between several instance of my generator service. so, I have also docker compose where I can have many instances of generator and redirector. I also have redis up. So, I would like to explore features of spring cloud as api gateway. Currently spring cloud as api gate way routes requests between 2 services. Explain to me advantage of such routing. Also I have configuration for rate limiting for generator service in api gateway. I don't allow more than 10 requests to generator service per minute. I would also like to explore other features of api gateway. to apply them and try. maybe caching? redis is up

sh /home/dik81/IdeaProjects/interview/jmetter/apache-jmeter-5.6.3/bin/jmeter.sh