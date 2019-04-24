package UserCFstep1;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



/**
 * 根据用户行为列表建评分矩阵
 * 输入 : 用户id 物品id 分值
 * 输出 : 物品id(行)_用户id(列)_值
 *
 * @author 90595
 *
 */
public class UserCFMR1 {
	private static String inPath = "/UserCF/step1_input/abc.txt";
	private static String outPath = "/UserCF/step1_output/";
	private static String hdfs = "hdfs://192.168.3.131:9000";

	public int run() {

		try {
			// 創建job配置類
			Configuration conf = new Configuration();
			// 配置hdfs的地址
			conf.set("fs.defaultFS", hdfs);
			// 創建一個job實列
			Job job = Job.getInstance(conf, "UserCFstep1");

			// 設置job的主類
			job.setJarByClass(UserCFMR1.class);
			// 設置job的mapper類的reducer類
			job.setMapperClass(UserCFMapper1.class);
			job.setReducerClass(UserCFReducer1.class);

			// 設置mapper輸出的類
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);

			// 設置reduce輸出的的累
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);

			//设置输入和输出路径
			FileSystem fs = FileSystem.get(conf);
			// 输入文件的路径
			Path inputPath = new Path(inPath);
			// 判断文件是否存在
			if (fs.exists(inputPath)) {
				FileInputFormat.addInputPath(job, inputPath);
			}
			// 作业完成后形成个输出路径 如果已经存在会报错
			Path outputPath = new Path(outPath);
			// 删除以存在路径
			fs.delete(outputPath, true);
			// 设置输出的路径 添加job中
			FileOutputFormat.setOutputPath(job, outputPath);
			// 返回运行状态 运行成功返回1 失败返回-1
			return job.waitForCompletion(true) ? 1 : -1;

		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		} catch (InterruptedException e) {
			e.printStackTrace();

		}
		return -1;
	}

	public static void main(String[] args) {
		int result = -1;
		result = new UserCFMR1().run();
		if (result == 1) {
			System.out.println("成功");
		} else if (result == -1) {
			System.out.println("失敗");
		}
	}
}
