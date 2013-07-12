package com.example.www;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import cc.mallet.fst.HMM;
import cc.mallet.fst.HMMTrainerByLikelihood;
import cc.mallet.fst.MaxLattice;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.PerClassAccuracyEvaluator;
import cc.mallet.fst.SumLattice;
import cc.mallet.fst.SumLatticeDefault;
import cc.mallet.fst.TransducerEvaluator;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.fst.ViterbiWriter;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SimpleTaggerSentence2TokenSequence;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.pipe.tsf.RegexMatches;
import cc.mallet.pipe.tsf.SequencePrintingPipe;
import cc.mallet.pipe.tsf.TokenTextCharSuffix;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.LabelSequence;
import cc.mallet.types.LabelVector;
import cc.mallet.types.Labels;
import cc.mallet.types.Sequence;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class TrainHMM {
	
	public TrainHMM(String trainingFilename, String testingFilename) throws IOException {

		PrintWriter hmmtrain = new PrintWriter("/home/nicolas/Desktop/hmmtrain.txt");
		PrintWriter hmmtest = new PrintWriter("/home/nicolas/Desktop/hmmtest.txt");
		
		List<Pipe> pipes = new ArrayList<Pipe>();

		pipes.add(new SimpleTaggerSentence2TokenSequence());
		pipes.add(new TokenTextCharSuffix("C1=", 1));
		pipes.add(new TokenSequence2FeatureSequence());

		Pipe pipe = new SerialPipes(pipes);

		
		//InstanceList trainingInstances = new InstanceList(pipe);
		//InstanceList testingInstances = new InstanceList(pipe);
		InstanceList instances = new InstanceList(new SerialPipes(pipes));
		instances.addThruPipe(new LineGroupIterator(new FileReader(trainingFilename),
	                Pattern.compile("BREAKLINE"), true));
		instances.shuffle(new Random());
        InstanceList testingInstances = instances.subList(0,instances.size()/2);
        InstanceList trainingInstances = instances.subList(instances.size()/2, instances.size());
        testingInstances.setPipe(new SequencePrintingPipe(hmmtest));
        hmmtest.close();
		//trainingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new FileInputStream(trainingFilename))), Pattern.compile("^\\s*$"), true));
		//testingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new FileInputStream(testingFilename))), Pattern.compile("^\\s*$"), true));
		
		HMM hmm = new HMM(pipe, null);
		hmm.addStatesForLabelsConnectedAsIn(trainingInstances);
		//hmm.addStatesForBiLabelsConnectedAsIn(trainingInstances);

		HMMTrainerByLikelihood trainer = new HMMTrainerByLikelihood(hmm);
		TransducerEvaluator trainingEvaluator = new PerClassAccuracyEvaluator(trainingInstances, "training");
		TransducerEvaluator testingEvaluator = new PerClassAccuracyEvaluator(testingInstances, "testing");
		
		/*HMMTrainerByLikelihood trainerByLikelihood = new HMMTrainerByLikelihood(hmm);
        TransducerEvaluator evaluator = new PerClassAccuracyEvaluator(test,"");
        trainerByLikelihood.addEvaluator(evaluator);
        trainerByLikelihood.train(train);*/		
        
        trainer.addEvaluator(testingEvaluator);
		trainer.train(trainingInstances, 10);
		
		trainingEvaluator.evaluate(trainer);
		testingEvaluator.evaluate(trainer);
		
//      save the trained model (if CRFWriter is not used)
//	    FileOutputStream fos = new FileOutputStream("/home/nicolas/Desktop/hmm.model");
//	    ObjectOutputStream oos = new ObjectOutputStream(fos);
//	    oos.writeObject(hmm);
//	    oos.close();
//	    
//	    hmm.print();
		

	    
//	    Label[] labels = new Label[] {};
//
//	    Sequence<Label> inputSeq = new LabelSequence(labels);
		
		TokenSequence ts = new TokenSequence();
		ts.add("позвони");
		ts.add("на");
		ts.add("номер");
		ts.add("891712");
		
		hmm.getInputAlphabet().startGrowth();
//		Instance ins = hmm.transduce(trainingInstances.get(0));
		
//	    Sequence inputSeq = new FeatureSequence(hmm.getInputAlphabet(), new int[] {0, 0, 0, 1 ,2});
	    
		MaxLattice lattice = new MaxLatticeDefault(hmm, ts.toFeatureSequence(hmm.getInputAlphabet()));
		Sequence<Object> res = lattice.bestOutputSequence();
		System.out.println(res.toString());
	}

	public static void main (String[] args) throws Exception {
//		TrainHMM trainer = new TrainHMM(args[0], args[1]);
		
		String train = "/home/nicolas/Desktop/out.txt";
		String test = "/home/nicolas/Desktop/out.txt";
		TrainHMM trainer = new TrainHMM(train, test);

	}

}