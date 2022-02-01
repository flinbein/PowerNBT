if [[ $VERSION == v* ]]
then
  export PLUGIN_VERSION=${VERSION:1}
else
  export PLUGIN_VERSION=${VERSION}-SNAPSHOT
fi
echo "exports PLUGIN_VERSION = $PLUGIN_VERSION"