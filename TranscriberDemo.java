/*
 * Copyright 2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */
 //HALO5
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat.Type;
import com.sun.speech.freetts.FreeTTS;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;



class Speaker {

    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;


    public void playSound(String filename){

        String strFilename = filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }
}



class Soung_Generator {



	static String text;


    public static void main(String tex) {

       // listAllVoices();

    	text = tex;

        FreeTTS freetts;
   AudioPlayer audioPlayer = null;
        String voiceName = "kevin16";

        System.out.println();
        System.out.println("Using voice: " + voiceName);

	System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");


        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice helloVoice = voiceManager.getVoice(voiceName);

        if (helloVoice == null) {
            System.err.println(
                "Cannot find a voice named "
                + voiceName + ".  Please specify a different voice.");
            System.exit(1);
        }
        else{
        helloVoice.allocate();
        }



       audioPlayer = new SingleFileAudioPlayer("/home/final-v8/Desktop/VOICEHELPER_REF/PlatformII/wind/wind",Type.WAVE);
//attach the audioplayer
       helloVoice.setAudioPlayer(audioPlayer);




        helloVoice.speak(text);


        helloVoice.deallocate();
//don't forget to close the audioplayer otherwise file will not be saved
        audioPlayer.close();

        Speaker q = new Speaker();
        q.playSound("/home/final-v8/Desktop/VOICEHELPER_REF/PlatformII/wind/wind.wav");

    }
}


public class TranscriberDemo {

    private static final String ACOUSTIC_MODEL =
        "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String DICTIONARY_PATH =
        "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH =
        "/home/final-v8/Desktop/VOICEHELPER_REF/PlatformII/wind";
    private static final String LANGUAGE_MODEL =
        "/home/final-v8/Desktop/VOICEHELPER_REF/PlatformII/wind/weather.lm";

    private static final Map<String, Integer> DIGITS =
        new HashMap<String, Integer>();

    static {
        DIGITS.put("oh", 0);
        DIGITS.put("zero", 0);
        DIGITS.put("one", 1);
        DIGITS.put("two", 2);
        DIGITS.put("three", 3);
        DIGITS.put("four", 4);
        DIGITS.put("five", 5);
        DIGITS.put("six", 6);
        DIGITS.put("seven", 7);
        DIGITS.put("eight", 8);
        DIGITS.put("nine", 9);
    }

    private static double parseNumber(String[] tokens) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < tokens.length; ++i) {
            if (tokens[i].equals("point"))
                sb.append(".");
            else
                sb.append(DIGITS.get(tokens[i]));
        }

        return Double.parseDouble(sb.toString());
    }
    private static void recognizeDigits(LiveSpeechRecognizer recognizer) {
        System.out.println("Digits recognition (using GrXML)");
        System.out.println("--------------------------------");
        System.out.println("Example: one two three");
        System.out.println("Say \"101\" to exit");
        System.out.println("--------------------------------");

        recognizer.startRecognition(true);
        while (true) {
            String utterance = recognizer.getResult().getHypothesis();
            if (utterance.equals("one zero one")
                || utterance.equals("one oh one"))
                break;
            else
                System.out.println(utterance);
        }
        recognizer.stopRecognition();
    }

    private static void recognizerBankAccount(LiveSpeechRecognizer recognizer) {
        System.out.println("This is bank account voice menu");
        System.out.println("-------------------------------");
        System.out.println("Example: balance");
        System.out.println("Example: withdraw zero point five");
        System.out.println("Example: deposit one two three");
        System.out.println("Example: back");
        System.out.println("-------------------------------");

        double savings = .0;
        recognizer.startRecognition(true);

        while (true) {
            String utterance = recognizer.getResult().getHypothesis();
            if (utterance.endsWith("back")) {
                break;
            } else if (utterance.startsWith("deposit")) {
                double deposit = parseNumber(utterance.split("\\s"));
                savings += deposit;
                System.out.format("Deposited: $%.2f\n", deposit);
            } else if (utterance.startsWith("withdraw")) {
                double withdraw = parseNumber(utterance.split("\\s"));
                savings -= withdraw;
                System.out.format("Withdrawn: $%.2f\n", withdraw);
            } else if (!utterance.endsWith("balance")) {
                System.out.println("Unrecognized command: " + utterance);
            }

            System.out.format("Your savings: $%.2f\n", savings);
        }

        recognizer.stopRecognition();
    }

    private static void recognizeWeather(LiveSpeechRecognizer recognizer) {
        System.out.println("Try some forecast. End with \"the end\"");
        System.out.println("-------------------------------------");
        System.out.println("Example: mostly dry some fog patches tonight");
        System.out.println("Example: sunny spells on wednesday");
        System.out.println("-------------------------------------");

        //Soung_Generator f = new Soung_Generator();

        recognizer.startRecognition(true);
        while (true) {
            String utterance = recognizer.getResult().getHypothesis();
            if (utterance.equals("the end"))
                break;
            else
            	System.out.println(utterance);
        }
        recognizer.stopRecognition();
    }


    public static void main(String[] args) throws Exception {

    	//Soung_Generator f = new Soung_Generator();

        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(true);

        configuration.setGrammarName("dialog");
        LiveSpeechRecognizer jsgfRecognizer =
            new LiveSpeechRecognizer(configuration);

        configuration.setGrammarName("digits.grxml");
        LiveSpeechRecognizer grxmlRecognizer =
            new LiveSpeechRecognizer(configuration);

        configuration.setUseGrammar(false);
        configuration.setLanguageModelPath(LANGUAGE_MODEL);
        LiveSpeechRecognizer lmRecognizer =
            new LiveSpeechRecognizer(configuration);

        jsgfRecognizer.startRecognition(true);

        System.out.println("ready to use");

        Soung_Generator f = new Soung_Generator();

        while (true) {
            System.out.println("Choose menu item:");
            System.out.println("Example: go to the bank account");
            System.out.println("Example: exit the program");
            System.out.println("Example: weather forecast");
            System.out.println("Example: digits\n");


            String utterance = jsgfRecognizer.getResult().getHypothesis();

            System.out.println("You say:"+utterance);

            if (utterance=="<unk>"){
            	System.out.println("no ideas");
            }
            else{
            	//f.main("You say:"+utterance);
            }

            if (utterance.startsWith("exit")){
            	System.out.println("good bye");
                break;
            }

            if (utterance.equals("bank")){
            	//System.out.println("nice  to see you");
            }

            if (utterance.equals("digits")) {
                /*jsgfRecognizer.stopRecognition();
                recognizeDigits(grxmlRecognizer);
                jsgfRecognizer.startRecognition(true);*/
            }

            if (utterance.equals("lights")) {

            	//java.lang.Runtime.getRuntime().exec("bash -c ./f1");

            	ProcessBuilder pb = new ProcessBuilder("bash", "./f1.sh");
                pb.inheritIO();
                Process process = pb.start();
                process.waitFor();

                f.main("lights swiched");
                /*jsgfRecognizer.stopRecognition();
                recognizerBankAccount(jsgfRecognizer);
                jsgfRecognizer.startRecognition(true);*/
            }

            if (utterance.endsWith("weather forecast")) {
                /*jsgfRecognizer.stopRecognition();
                recognizeWeather(lmRecognizer);
                jsgfRecognizer.startRecognition(true);*/
            }
        }

        //jsgfRecognizer.stopRecognition();
    }
}
