export PLUGIN_VERSION=`cat ./PLUGIN_VERSION.var`
echo "build plugin-version=$PLUGIN_VERSION"
env PLUGIN_VERSION="$PLUGIN_VERSION" mvn -Dplugin-version=$PLUGIN_VERSION clean install -DskipTests