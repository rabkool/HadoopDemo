package UserCFstep3;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

//1 reducer输入key的类型 与mapper输出的key一致 2 reducer输入value类型  和mapper类输入的value类型一致
//3 reducer输出key的类型  4 reducer输出value类型 都为text

public class UserCFReducer3 extends Reducer<Text, Text, Text, Text> {
	// 创建两个私有变量
	// 输出的key
	private Text outKey = new Text();
	// 输出的value
	private Text outValue = new Text();

	// 重写reduce
	// 输入的key是mapper类输出的key					key:列号
	// 输入的value集合是mapper类输出的value集合 		value:[行号_值,行号_值,行号_值]
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		StringBuilder sb = new StringBuilder();

		// 对value的集合做个循环 每一个text行号加下划线加列号 text:行号_値
		for (Text text : values) {
			// 拼接字符串
			sb.append(text + ",");
		}
		
		// 行末多余逗号进行删除
		String line = null;
		// 如果以逗号结尾
		if (sb.toString().endsWith(",")) {
			// 长度减一
			line = sb.substring(0, sb.length() - 1);
		}

		// 设置输出的key
		outKey.set(key);
		// 设置value的key
		outValue.set(line);
		// 进行输出
		context.write(outKey, outValue);
	}
}