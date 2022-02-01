if [[ $VERSION == v* ]]
then
  env PLUGIN_VERSION=${VERSION:1} mvn package
else
  env PLUGIN_VERSION=${VERSION} mvn package
fi