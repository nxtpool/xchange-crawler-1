docker stop crawler
docker rm crawler
docker rmi -f crawler
docker build -t crawler .
docker run -d --restart="always" --net host --name crawler crawler
docker attach --sig-proxy=false crawler
