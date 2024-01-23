package sbt.qsecure.monitoring.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sbt.qsecure.monitoring.vo.CommonSettingVO;
import sbt.qsecure.monitoring.vo.ConvExitVO;

@Service
public interface SettingService {
	public List<ConvExitVO> getConvExitList();
	public List<CommonSettingVO> getCommonSettingList();
}
