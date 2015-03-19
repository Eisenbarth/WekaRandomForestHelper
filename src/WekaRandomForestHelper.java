import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Random;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaRandomForestHelper {
    private final static Parameters parameters = new Parameters();

    public static void main(String... args) {

        final JCommander jc = new JCommander(parameters);
        jc.addCommand("classify", parameters.commandClassify);
        jc.addCommand("build", parameters.commandBuild);
        parseCmdLineArgs(jc, args);

        if (jc.getParsedCommand().equalsIgnoreCase("build")) {
            String arffFilename = parameters.commandBuild.getArffFilename();
            String classifierModel = parameters.commandBuild.getClassifierModel();
            WekaRandomForestHelper.runBuilder(arffFilename, classifierModel);
        } else if (jc.getParsedCommand().equalsIgnoreCase("classify")) {
            String classifierModel = parameters.commandClassify.getClassifierModel();
            List<String> arffFilenames = parameters.commandClassify.getArffFilenames();
            List<String> LabeledArffFilenames = parameters.commandClassify.getLabeledArffFilenames();
            WekaRandomForestHelper.runClassifier(classifierModel, arffFilenames, LabeledArffFilenames);
        }
    }

    private static void parseCmdLineArgs(JCommander jc, String ... args) {
        try {
            jc.parse(args);
        } catch (ParameterException pe) {
            System.out.println(pe.getMessage());
            jc.usage();
            System.exit(1);
        }
        if (parameters.isHelpSet()) {
            jc.setProgramName(WekaRandomForestHelper.class.getName());
            jc.usage();
            System.exit(0);
        }
    }

    private static void runBuilder(String arffFilename, String classifierModelFilename) {
        String trainDataFilepath = arffFilename;

        File file = new File(trainDataFilepath);
        InputStream inputStream;
        DataSource source;
        Instances data;
        Classifier classifier = new RandomForest();

        try {
            inputStream = new java.io.FileInputStream(file);

            source = new DataSource(inputStream);
            data = source.getDataSet();
            if (data.classIndex() == -1)// set which attribute is the outcome
                                        // variable
                data.setClassIndex(data.numAttributes() - 1);

            classifier.setOptions(getOptions());
            classifier.buildClassifier(data);

            printEvaluation(classifier, data);

            // serialize classifier
            SerializationHelper.write(getClassifierModelFileName(classifierModelFilename), classifier);

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getClassifierModelFileName(String classifierModelFilename) {
        System.out.println(classifierModelFilename);
        if(classifierModelFilename == null)
            return "resources/classifier.model";
        else
            return classifierModelFilename;
    }

    private static void runClassifier(String classifierModelFilename, List<String> arffFilenames, List<String> labeledArffFilenames) {
        try {
            Classifier classifier = (Classifier) weka.core.SerializationHelper.read(classifierModelFilename);

            System.out.println("The classifier is running.");
            for (int i = 0 ; i < arffFilenames.size() ; i++) {
                String arffFilename = arffFilenames.get(i);
                // load unlabeled data
                Instances unlabeled = new Instances(new BufferedReader(new FileReader(arffFilename)));

                // set class attribute
                unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

                // create copy
                Instances labeled = new Instances(unlabeled);

                // label instances
                for (int j = 0; j < unlabeled.numInstances(); j++) {
                    double clsLabel = classifier.classifyInstance(unlabeled.instance(j));
                    labeled.instance(j).setClassValue(clsLabel);
                }
                
                String outputFile = null;
                if(labeledArffFilenames == null || labeledArffFilenames.get(i) == null) {
                    String[] tokens = arffFilename.split("\\.");
                    // for(String s : tokens){
                    // System.out.println(s);
                    // }
                    tokens[0] = tokens[0] + "Labeled";
                    outputFile = Utils.join(tokens, ".");
                } else {
                    outputFile = labeledArffFilenames.get(i);
                }
                // save labeled data
                // System.out.println(arffFilename);
                
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
                writer.write(labeled.toString());
                writer.newLine();

                writer.flush();

                writer.close();
            }
            System.out.println("Classification is finished.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void printEvaluation(Classifier classifier, Instances data) throws Exception {
        printEvaluation(classifier, data, false);
    }

    private static void printEvaluation(Classifier classifier, Instances data, boolean isNewDataSet) throws Exception {
        Evaluation eval = new Evaluation(data);
        if (isNewDataSet)
            eval.evaluateModel(classifier, data);
        else
            eval.crossValidateModel(classifier, data, 10, new Random(1));
        System.out.println(eval.toSummaryString("\n=== Summary ===\n", true));
        System.out.println("detailed : " + eval.toClassDetailsString());
        System.out.println(eval.toMatrixString("\n=== Confusion Matrix ===\n"));

    }

    private static String[] getOptions() {
        String[] options = new String[8];
        options[0] = "-I";
        options[1] = "10";  // number of trees
        options[2] = "-K";
        options[3] = "0";   // number of features
        options[4] = "-S";
        options[5] = "1";   // seed
        options[6] = "-depth";
        options[7] = "0";   // 0 for unlimited

        return options;
    }
}
