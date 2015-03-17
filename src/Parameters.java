import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class Parameters {
    @Parameter(names = { "--help", "-h" }, description = "print this message", help = true)
    private boolean help = false;
    
    public CommandBuild commandBuild = new CommandBuild();
    
    public CommandClassify commandClassify = new CommandClassify();
    
    public boolean isHelpSet() {
        return this.help;
    }
    
    @com.beust.jcommander.Parameters(separators = "=", commandDescription = "Build a Random Forest classifier with the given arff file.")
    public class CommandBuild {
        @Parameter(names={"--data", "-d"},description = "The file used to train the classifier.",required=true)
        private String ArffFilename;

        public String getArffFilename() {
            return ArffFilename;
        }
    }
    
    @com.beust.jcommander.Parameters(separators = "=", commandDescription = "Build a Random Forest classifier with the given arff file.")
    public class CommandClassify {
        @Parameter(description="Arff files that you want to classify.", required=true)
        private List<String> arffFilenames;

        @Parameter(names={ "--classifier", "-c" },description = "The classifier model to use.",required=true)
        private String classifierModel;
        
        public List<String> getArffFilenames() {
            return arffFilenames;
        }

        public String getClassifierModel() {
            return classifierModel;
        }
    }

/*    @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
    private Integer verbose = 1;
*/

}
