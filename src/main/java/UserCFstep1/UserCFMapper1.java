package UserCFstep1;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//mapeer 1 行号 对于long类型  2 map输入的value类型 文本 (Text) 
//3 map输出的key类型 文本(Text) 4 map出入的value类型 文本(Text)
public class UserCFMapper1 extends Mapper<LongWritable, Text, Text, Text> {

	// 输出的key
	private Text outKey = new Text();
	// 输出的value
	private Text outValue = new Text();

	/**
	 * key:1   2 ....
	 * value:A,1,1	  C,3,4....
	 */
	
	//重写map方法
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {

		// 文本用逗号分割
		String[] values = value.toString().split(",");
		// 第一个元素用户id
		String userID = values[0];
		// 第二个元素物品id
		String itemID = values[1];
		// 第三个元素分值
		String score = values[2];

		// 以用户id作为行
		outKey.set(userID);
		// 商品id作为列
		outValue.set(itemID + "_" + score);

		// 输出
		context.write(outKey, outValue);
	}

}
