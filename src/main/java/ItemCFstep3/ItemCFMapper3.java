package ItemCFstep3;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//mapeer 1 行号 对于long类型  2 map输入的value类型 文本 (Text) 
//3 map输出的key类型 文本(Text) 4 map出入的value类型 文本(Text)
public class ItemCFMapper3 extends Mapper<LongWritable, Text, Text, Text> {

	
	// 输出的key
	private Text outKey = new Text();
	// 输出的value
	private Text outValue = new Text();

	/** 
	 *  key:列号	 value:[行号_值,行号_值,行号_值]
	 *  数据
	 *     1	A_2,C_5
	 *	   2	A_10,B_3
     *     3	C_15
	 */
	// 重写map方法
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		// 定义字符串数组将value转换成字符串类型调用split以\t分割 (\t等于tab
		String[] rowAndLine = value.toString().split("\t");

		// 获得矩阵的行号
		String row = rowAndLine[0];

		// 对字符串以,号进行拆分
		String[] lines = rowAndLine[1].split(",");

		// lines.length 右侧数组长度
		for (int i = 0; i < lines.length; i++) {
			// 数组中的每一个元素 再次进行拆分 分隔符是下划线
			// 列号
			String column = lines[i].split("_")[0];
			// 具体值
			String valueStr = lines[i].split("_")[1];
			// key:列号 value:行号_值 矩阵转置 行转列 列转行 列号为key值 去重
			outKey.set(column);
			outValue.set(row + "_" + valueStr);
			// 输出
			context.write(outKey, outValue);
			
			/**
			 *	输出结果
			 *  A	2_10,1_2
			 *	B	2_3
			 *	C	3_15,1_5
			 */
		}

	}

}
