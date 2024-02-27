package sbt.qsecure.monitoring.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sbt.qsecure.monitoring.mapper.CommandMapper;
import sbt.qsecure.monitoring.mapper.ServerMapper;
import sbt.qsecure.monitoring.mapper.SettingMapper;
import sbt.qsecure.monitoring.vo.ConvExitVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService{
	
	private final SettingMapper settingMapper;
	
	
	@Override
	public List<ConvExitVO> getConvExitList() {

		return settingMapper.getConvExitList();
	}

//	@Override
//	public List<CommonSettingVO> getCommonSettingList() {
//		// TODO Auto-generated method stub
//		return settingMapper.getCommonSettingList();
//	}

}
