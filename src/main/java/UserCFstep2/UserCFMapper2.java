package UserCFstep2;

import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class UserCFMapper2 extends Mapper<LongWritable, Text, Text, Text> {

	private Text outKey = new Text();
	private Text outValue = new Text();

	//java容器
	private List<String> cacheList = new ArrayList<String>();

	//保留2未小数
	private DecimalFormat df = new DecimalFormat("0.00");

	// 重写map类的初始化方法
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		try {
			// 通过输入流将全局缓存中的 右侧j矩阵读入List<String>中
			// FileReader fr = new FileReader(MR2.hdfs+MR2.cache);
			// BufferedReader br = new BufferedReader(fr);
			FileSystem fs = FileSystem.get(URI.create(UserCFMR2.hdfs + UserCFMR2.cache), context.getConfiguration());

			FSDataInputStream in = fs.open(new Path(UserCFMR2.hdfs + UserCFMR2.cache));
			// FSDataInputStream in = fs.open

			// 每行的格式是 行tab 列-值,列值,列-值,列值
			
			//定义变量读缓存的每一行
			String line = null;
			while ((line = in.readLine()) != null) {
				//添入cacheList容器
				cacheList.add(line);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param key : 行号
	 * @param value : 行tab 列_值,列_值,列_值,列_值
	 */
	//map方法循环体
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		//左侧
		// 左侧矩阵的第一行  value按tab进行分割   
		String row_matrix1 = value.toString().split("\t")[0];
		// 列_值(数组) 第二个元素用逗号进行分割
		String[] column_value_array_matrix1 = value.toString().split("\t")[1].split(",");

		
		// 计算左侧矩阵行的空间距离
		double denominator1 = 0;
		for (String column_value : column_value_array_matrix1) {
			//获得分值_分割
			String score = column_value.split("_")[1];
			//对分值评分进行累加
			denominator1 += Double.valueOf(score) * Double.valueOf(score);
		}
		//对变量开平分
		denominator1 = Math.sqrt(denominator1);
		
		
		//对全局缓存进行循环
		for (String line : cacheList) {
			//右侧
			// line右侧矩阵的每一行
			//格式 行	  tab 列_值,列_值,列_值,列_值
			//右侧矩阵每一行行号   tab分割
			String row_matrix2 = line.toString().split("\t")[0];
			//右侧数组 逗号风格
			String[] column_value_array_matrix2 = line.toString().split("\t")[1].split(",");
			

			// 计算左侧矩阵的空间距离
			double denominator2 = 0;
			for (String column_value : column_value_array_matrix2) {
				//获得分值_分割
				String score = column_value.split("_")[1];
				//对分值评分进行累加
				denominator2 += Double.valueOf(score) * Double.valueOf(score);
			}
			//对变量开平分
			denominator2 = Math.sqrt(denominator2);
			
			// 矩阵两行相乘的结果
			int numerator = 0;
			// 遍历左矩阵第一行的每一列
			for (String column_value_matrix1 : column_value_array_matrix1) {
				//获得列号 下划线分割
				String column_matrix1 = column_value_matrix1.split("_")[0];
				//列号所对应的值下划线分割
				String value_matrix1 = column_value_matrix1.split("_")[1];

				//遍历右侧矩阵第一行的每一列
				for (String column_value_matrix2 : column_value_array_matrix2) {
					//如果右侧矩阵的列和左侧矩阵的列是相等的  
					if (column_value_matrix2.startsWith(column_matrix1 + "_")) {
						//取出这一列所对应的值 下划线进行分割
						String value_matrix2 = column_value_matrix2.split("_")[1];
						//将两列的值相乘并累加
						numerator += Integer.valueOf(value_matrix1) * Integer.valueOf(value_matrix2);
					}
				}
			}
			//numerator是结果矩阵中的某元素 坐标为 行:row_matrix1 , 列 : row_matrix2(右侧矩阵已转换)
			
			//余弦相似度 
			double cos = numerator / (denominator1 * denominator2);
			//等于0跳出循环
			if (cos == 0) {
				continue;
			}
			
			//输出的key值     第一个矩阵的行
			outKey.set(row_matrix1);
			//输出的value值       第二个矩阵的行 加下划线  所对应的值 保留2未小数
			outValue.set(row_matrix2 + "_" + df.format(cos));

			//输出    key : 行 value : 列_值
			context.write(outKey, outValue);
		}

	}
}
