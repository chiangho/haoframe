package hao.framework.core.utils;

import java.io.File;

/***
 * 文件操作工具
 * 
 * @author chianghao
 *
 */
public class FileUtils {

	/***
	 * 创建子目录
	 * 
	 * @param parent
	 * @param name
	 * @return
	 */
	public static File createChildDir(File parent, String name) {
		File file = new File(parent, name);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

}
