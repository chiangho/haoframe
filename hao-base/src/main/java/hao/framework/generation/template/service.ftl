${pack}

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import hao.framework.core.expression.HaoException;
import hao.framework.core.sequence.Seq;
import hao.framework.db.page.Page;
import hao.framework.db.page.PageHelper;
import hao.framework.web.ActionContext;
import hao.framework.web.form.Form;

${importFiles}


@Service
public class ${className} {

	
	@Autowired
	${dao}
	
	
	${functionGetPageList}
	${functionGetList}
	${functionQueryById}
	${functionDelById}
	${functionSaveForm}
	
		
}
