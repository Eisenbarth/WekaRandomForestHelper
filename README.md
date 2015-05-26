WekaRandomForestHelper
======================

WekaRandomForestHelper is a java application that help you to build random forest models and classify some data.

# Dependencies

WekaRandomForestHelper uses the jar of Weka which can be found [here](http://www.cs.waikato.ac.nz/ml/weka/downloading.html) (The zip archive in the « other platform » section).

It also uses the jar of Jcommander which can be build from the sources found [here](https://github.com/cbeust/jcommander)

# Usage

### Compile it

```bash
cd src
javac *.java -d ../bin -cp ".:path/to/jcommander.jar:path/to/weka.jar"
```

### Run it

```bash
java -cp ".:path/to/jcommander.jar:path/to/weka.jar:bin" WekaRandomForestHelper [arguments]
```

### Arguments

There is two command that can be used with this application :
* build
* classify

The build command takes an extra argument :
the arff file used to train the model (--data or -d flag)

The classify command takes at least two extra argument :
* the classifier model (--classifier or -c flag)
* at least one arff file of which you want to classify the data

### Exemple

(assuming all the required jars are in the classpath)

```bash
java WekaRandomForestHelper build --data=resources/weather.nominal.arff
```

```bash
java WekaRandomForestHelper classify --classifier=resources/classifier.model resources/testweather.nominal.arff
```
