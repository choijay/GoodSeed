package com.common.syscommon.utility;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import goodseed.core.common.model.GoodseedDataset;
import goodseed.core.common.model.Parameters;


/**
 * The class TreeDatasetUtil<br>
 * <br>
 * HTML jqgrid tree dataset dataset을 가공하는 유틸<br>
 * <br>
 *
 */
public class TreeDatasetUtil {

	private static final String IDX = "IDX";
	private static final String TREELEAF = "TREELEAF";
	private static final String TREEPARENT = "TREEPARENT";
	private static final String TREELEAFLVL = "TREELEAFLVL";
	private static final String TREEEXP = "TREEEXP";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	/**
	 * 
	 * @param inParams
	 * @return
	 */
	public static GoodseedDataset makeTreeDataset(GoodseedDataset treeDs, Parameters inParams) {
		// treeFactor로 사용 할 정보를 추출한다.
		String id = inParams.getVariableAsString("treeFactorId");
		String lvl = inParams.getVariableAsString("treeFactorLvl");
		String lvlno = inParams.getVariableAsString("treeFactorLvlno");
		
		// 트리 데이터셋을 구성한다.
		setTreeFactor(0, 0, -1, treeDs, id, lvl, lvlno);

		return treeDs;
	}

	/**
	 * get parent id by level
	 * @param idx
	 * @param prevLvl
	 * @param prevId
	 * @param ds
	 * @param id
	 * @param lvl
	 * @return
	 */
	private static Map setTreeFactor(int idx, int prevLvl, int prevId, GoodseedDataset ds, String id, String lvl, String lvlno) {
		Map rstMap = new HashMap<String, Object>();
		if((idx == ds.getRowCount() || prevLvl > getValueAsInt(ds, idx, lvl))) { 
			//자식노드에 도달했을 때, 자식노드의 정보를 리턴
			int rstIdx = idx == ds.getRowCount() ? idx - 1 : idx;
			rstMap.put(IDX, rstIdx);
			rstMap.put(TREELEAF, TRUE);
			return rstMap;
		} else {
			// 현재 노드에 대한 정보
			// lvl : 트리레벨(progLvl) 
			// id  : 트리id (rownum)
			int curLvl = getValueAsInt(ds, idx, lvl);
			int curId = getValueAsInt(ds, idx, id);
			int prtId = -1;

			if(idx + 1 < ds.getRowCount()) {
				// 가장 마지막 row에 대한처리
				prtId = (curLvl == getValueAsInt(ds, idx + 1, lvl)) ? prevId : curId;
			}
			
			//자신이 갖고 있는 최후 node에 대한 정보 반환
			rstMap = setTreeFactor(idx + 1, curLvl, prtId, ds, id, lvl, lvlno);

			int rstIdx = ((Integer)rstMap.get(IDX)).intValue();
			int rstLeafLvl = rstMap.get(TREELEAFLVL) == null ? -1 : ((Integer)rstMap.get(TREELEAFLVL)).intValue();
			boolean rstLeaf = Boolean.valueOf(rstMap.get(TREELEAF).toString());

			if(rstLeaf || rstLeafLvl == curLvl) { 
				// 자식 노드 정보 셋팅
				ds.setColumn(idx, TREELEAF, TRUE);
				ds.setColumn(idx, TREEPARENT, prevId);
				ds.setColumn(idx, TREEEXP, FALSE);
				ds.setColumn(idx, "id", getValueAsInt(ds, idx, "ROWNUM"));

				rstMap.clear();
				rstMap.put(IDX, rstIdx);
				rstMap.put(TREELEAF, FALSE);
				rstMap.put(TREELEAFLVL, curLvl);

				return rstMap;
			} else { 
				// 부모노드 정보 셋팅
				if(ds.getColumnAsString(idx+1, lvlno).indexOf(ds.getColumnAsString(idx, lvlno))==-1){
					ds.setColumn(idx, TREELEAF, TRUE);
				}else{
					ds.setColumn(idx, TREELEAF, FALSE);
				}
				ds.setColumn(idx, TREEPARENT, prevId);
				ds.setColumn(idx, TREEEXP, (curLvl>=3? FALSE:TRUE)); //3레벨이하 접어서 로딩
				ds.setColumn(idx, "id", getValueAsInt(ds, idx, "ROWNUM")); //treegrid 필수항목
				
				if(!rstLeaf && (curLvl == getValueAsInt(ds, ((Integer)rstMap.get(IDX)).intValue(), lvl))) {
					// parent 노드가 있을 경우, 한번 더 노드 순회
					return setTreeFactor(rstIdx, curLvl, prevId, ds, id, lvl, lvlno);
				} else {
					// 이외의 경우, 순회 종료
					rstMap.clear();
					rstMap.put(IDX, rstIdx);
					rstMap.put(TREELEAF, FALSE);
					return rstMap;
				}
			}
		}
	}

	/**
	 * get dataset value
	 * @param ds
	 * @param idx
	 * @param key
	 * @return
	 */
	private static int getValueAsInt(GoodseedDataset ds, int idx, String key) {
		return toInt(((Map<String, Object>)ds.getRowAsMap(idx)).get(key));
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	private static int toInt(Object obj) {
		BigDecimal bd = new BigDecimal(obj.toString());
		return bd.intValue();
	}
}

