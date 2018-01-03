package com.alogic.cache.xscript;

import org.apache.commons.lang3.StringUtils;

import com.alogic.cache.CacheObject;
import com.alogic.load.Store;
import com.alogic.xscript.ExecuteWatcher;
import com.alogic.xscript.Logiclet;
import com.alogic.xscript.LogicletContext;
import com.alogic.xscript.doc.XsObject;
import com.alogic.xscript.plugins.Segment;
import com.anysoft.util.BaseException;
import com.anysoft.util.Properties;
import com.anysoft.util.PropertiesConstants;

/**
 * 缓存定位
 * 
 * @author yyduan
 * @since 1.6.11.6
 */
public class CacheLocate extends Segment {
	
	/**
	 * 缓存id
	 */
	protected String id = "id";
	
	/**
	 * 父节点的上下文id
	 */
	protected String pid = "$cache";
	
	/**
	 * 当前节点的上下文id
	 */
	protected String cid = "$cache-object";
	
	public CacheLocate(String tag, Logiclet p) {
		super(tag, p);
	}

	@Override
	public void configure(Properties p){
		super.configure(p);
		id = PropertiesConstants.getRaw(p, "id", "");		
		pid = PropertiesConstants.getString(p,"pid", pid);
		cid = PropertiesConstants.getString(p,"cid", cid);
	}	
	
	@Override
	protected void onExecute(XsObject root,XsObject current, LogicletContext ctx,
			ExecuteWatcher watcher) {
		Store<CacheObject> cache = ctx.getObject(pid);
		if (cache == null){
			throw new BaseException("core.e1001","It must be in a cache context,check your together script.");
		}
		
		String idValue = ctx.transform(id);
		if (StringUtils.isNotEmpty(idValue)){
			CacheObject found = cache.load(idValue,true);
			if (found == null){
				throw new BaseException("clnt.e2007","Can not find object,id=" + idValue);
			}
			
			try {
				ctx.setObject(cid, found);
				super.onExecute(root, current, ctx, watcher);
			}finally{
				ctx.removeObject(cid);
			}
		}
	}

}