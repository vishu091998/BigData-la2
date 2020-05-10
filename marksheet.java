package my.mapred.pack;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class StudentMarksheet {
	
	//MAPPER CLASS
		public static  class Map extends MapReduceBase implements Mapper<LongWritable,Text,Text,IntWritable>{
		private final static IntWritable one=new IntWritable(1);
		public void map(LongWritable key,Text value,OutputCollector<Text,IntWritable> output,Reporter reporter)throws IOException{
		String myString=value.toString();
		String[] userCount=myString.split(",");
	//1. Total number of students who have scored more than 60 in Subject 1
		if(Integer.parseInt(userCount[3])>60)
		{
			output.collect(new Text("score>60"),one);
		}
	// 2. Total number of students who have passed in all the subjects  
		if(userCount[5].equals("YES"))
		{
		output.collect(new Text("PASSED"),one);
		}
	       }
	}
		
	//REDUCER CLASS
		public static class Reduce extends MapReduceBase implements Reducer<Text,IntWritable,Text,IntWritable>{
		public void reduce(Text key,Iterator<IntWritable> values,OutputCollector<Text,IntWritable> output,Reporter reporter)throws IOException{
			int finaluserCount=0;
			Text myKey=key;
			while(values.hasNext())
			{
				IntWritable value =values.next();
				finaluserCount += value.get();	
			}
			output.collect(myKey, new IntWritable(finaluserCount));	
		}	
	}
		//DRIVER CONFIGURATION
		public static void main(String[] args)throws Exception{
			JobConf conf = new JobConf(StudentMarksheet.class);
			conf.setJobName("studentmarksheet");
			conf.setOutputKeyClass(Text. class);
			conf.setOutputValueClass(IntWritable.class);
			conf.setMapperClass(Map.class);
			conf.setCombinerClass(Reduce.class);
			conf.setReducerClass(Reduce.class);
			conf.setInputFormat(TextInputFormat.class);
			conf.setOutputFormat(TextOutputFormat.class);
			FileInputFormat.setInputPaths(conf, new Path(args[0]));
			FileOutputFormat.setOutputPath(conf,new Path(args[1]));
			JobClient.runJob(conf);
			}
}

