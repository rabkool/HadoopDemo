package UserCFstep5;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/**
 * 根据评分矩阵 将step4的输出中 将用户已经有过行为置0
 * 输入 : step4的输出
 * 缓存 : step1的输出
 * 输出 : 用户id(行)_物品id(列)_分值 
 * 最终的推荐列表
 * 
 * @author 90595
 *
 */
public class UserCFMR5 {
	private static String inPath = "/UserCF/step4_output";
	private static String outPath = "/UserCF/step5_output";
	public static String cache = "/UserCF/step1_output/part-r-00000";
	public static String hdfs = "hdfs://192.168.3.131:9000";

	public int run() {

		try {
			// 創建job配置類
			Configuration conf = new Configuration();
			// 配置hdfs的地址
			conf.set("fs.defaultFS", hdfs);
			// 創建一個job實列
			Job job = Job.getInstance(conf, "UserCFstep5");

			job.addCacheFile(new URI(cache + "#UserScore3"));

			// 設置job的主類
			job.setJarByClass(UserCFMR5.class);
			// 設置job的mapper類的reducer類
			job.setMapperClass(UserCFMapper5.class);
			job.setReducerClass(UserCFReducer5.class);

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

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public static void main(String[] args) {
		int result = -1;
		result = new UserCFMR5().run();
		if (result == 1) {
			System.out.println("成功");
		} else if (result == -1) {
			System.out.println("失敗");
		}
	}
}
