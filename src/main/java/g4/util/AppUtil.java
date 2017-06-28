package g4.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import g4.AppMode;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;

/**
 * Created by marlanti on 6/26/17.
 */
public class AppUtil {

    public static File getDir(CommandLine cmd){
        File dir = null;
        if(!cmd.hasOption("dir")){
            dir = Paths.get(System.getProperty("user.dir")).toFile();
        }else{
            dir = Paths.get(cmd.getOptionValue("dir")).toFile();
        }

        return dir;
    }


    private static Options commandLineOptions() {
        Options options = new Options();
        Option k = Option.builder("k")
                .required()
                .hasArg()
                .desc("The k parameter of the Shamir schema")
                .build();
        Option n = Option.builder("n")
                .hasArg()
                .desc("The number of shadows to generate (must be less than or equal " +
                        "to the amount of files in the directory specified by -dir")
                .build();
        Option d = Option.builder("d")
                .desc("Distribute in carrier images (used with -n)")
                .build();
        Option r = Option.builder("r")
                .desc("Recover from a set of carrier images")
                .build();
        Option secret = Option.builder("secret")
                .hasArg()
                .required()
                .desc("The image to hide (if used with -d), or the file to write " +
                        "the output to (if used with -r)")
                .build();
        Option dir = Option.builder("dir")
                .hasArg()
                .desc("The directory of the carrier images (if used with -d), " +
                        "or the directory containing the carrier images to " +
                        "recover the secret from")
                .build();
        return options.addOption(k)
                .addOption(n)
                .addOption(d)
                .addOption(r)
                .addOption(secret)
                .addOption(dir);
    }

    public static CommandLine getCommandLine(String[] args) throws ParseException {
        CommandLine cmd = new DefaultParser().parse(commandLineOptions(), args);
        String mode = getMode(cmd);
        validateModeOptions(mode, cmd);
        return cmd;
    }

    private static String getMode(CommandLine cmd) {
        Boolean distribute = cmd.hasOption("d");
        Boolean recover = cmd.hasOption("r");
        if (recover == distribute) {
            System.err.println("Either -d or -r must be specified");
            System.exit(1);
        }
        return distribute ? "d" : "r";
    }

    private static void validateModeOptions(String mode, CommandLine cmd) {

        if(mode == "r" && cmd.hasOption("n")){
            System.err.println("-n parameter can't be used in r mode.");
            System.exit(1);
        }

        if(mode == "d" && cmd.hasOption("n")){

        }

        if (!cmd.hasOption("k")) {
            System.err.println("-k parameter is required for distributing.");
            System.exit(1);
        }

        if(!cmd.hasOption("secret")){
            System.err.println("-secret parameter is required.");
            System.exit(1);
        }
    }

    public static File getSecretFile(CommandLine cmd) {
        String mode = getMode(cmd);
        File secretFile = null;

        try {
            secretFile = Paths.get(cmd.getOptionValue("secret")).toFile().getCanonicalFile();
            if (!FilenameUtils.getExtension(secretFile.getName()).equals("bmp")) {
                System.err.println("-secret parameter must be a bmp file");
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Secret file not found.");
            if (mode == "d") {
                System.err.println("Secret file not found.");
            }
            System.exit(1);
        }
        return secretFile;
    }


    public static int getN(CommandLine cmd, File dir) {
        int n = 0;
        int bmpFilesInDirectory = countBmpFilesInDirectory(dir);

        if(!cmd.hasOption("n")){
            n = bmpFilesInDirectory;
        }else{
            n = Integer.valueOf(cmd.getOptionValue("n"));
        }

        validateN(n, bmpFilesInDirectory);
        return n;
    }

    private static void validateN(int n, int bmpFileInDirectory) {
        if(n < 2){
            System.err.println("n value can't be lower than 2.");
            System.exit(1);
        }
        if(n != bmpFileInDirectory){
            System.err.println("n value is different from bmp files in directory.");
            System.exit(1);
        }
    }

    private static void validateK(int k, int n){
        if(k > n || k < 2){
            System.err.println("k value must be between 2 and n value.");
            System.exit(1);
        }
    }

    public static int getK(CommandLine cmd, int n){
        int k = Integer.valueOf(cmd.getOptionValue("k"));
        validateK(k,n);

        return k;
    }

    private static int countBmpFilesInDirectory(File dir){
        int n = 0;
        for(File file : dir.listFiles()){
            if(FilenameUtils.getExtension(file.getName()).equals("bmp")) {
                n++;
            }
        }
        return n;
    }
}
