package sbt.qsecure.monitoring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import sbt.qsecure.monitoring.vo.CommonSettingVO;
import sbt.qsecure.monitoring.vo.ConvExitVO;

@Mapper
public interface SettingMapper {
	public List<ConvExitVO> getConvExitList();
	public List<CommonSettingVO> getCommonSettingList();
}
