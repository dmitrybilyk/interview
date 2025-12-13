const { MongoClient } = require("mongodb");

async function run() {
    // TODO:
    // Replace the placeholder connection string below with your
    // Altas cluster specifics. Be sure it includes
    // a valid username and password! Note that in a production environment,
    // you do not want to store your password in plain-text here.
    const uri =
        // "mongodb://dmytro:035645@ac-qs0438f-shard-00-00.jl1clmw.mongodb.net:27017,ac-qs0438f-shard-00-01.jl1clmw.mongodb.net:27017,ac-qs0438f-shard-00-02.jl1clmw.mongodb.net:27017/?replicaSet=atlas-14dx0d-shard-0&ssl=true&authSource=admin"
        // 'mongodb+srv://dmytro:035645@cluster.jl1clmw.mongodb.net/?retryWrites=true&w=majority&tls=true&tlsAllowInvalidCertificates=false'
        "mongodb://admin:admin123@localhost:27017"
    // The MongoClient is the object that references the connection to our
    // datastore (Atlas, for example)
    const client = new MongoClient(uri);

    // The connect() method does not attempt a connection; instead it instructs
    // the driver to connect using the settings provided when a connection
    // is required.
    await client.connect();
    console.log('connection was successfulllllllllllllllllll')

    // Provide the name of the database and collection you want to use.
    // If the database and/or collection do not exist, the driver and Atlas
    // will create them automatically when you first write data.
    const dbName = "sample_guides";
    const collectionName = "planets";

    // Create references to the database and collection in order to run
    // operations on them.
    const database = client.db(dbName);
    const collection = database.collection(collectionName);
    const cursor = collection.find();
    await cursor.forEach(console.log)



    // Make sure to call close() on your client to perform cleanup operations
    await client.close();
}
run().catch(console.dir);