package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 * 作者：ljs
 * 日期：2024/8/4 19:44
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询营业额
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        String listToString = StringUtils.join(dateList, ",");//将集合拼成字符串 用逗号相连

        //计算每天对应的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //找到开始日期具体时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //找到结束日期具体时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询营业额
            Map map = new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status",Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);

        }

        //封装返回结果
        return TurnoverReportVO.builder()
                        .dateList(listToString)
                        .turnoverList(StringUtils.join(turnoverList,","))
                        .build();
    }

    /**
     * 查询用户数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //存放从begin到end的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        String listToString = StringUtils.join(dateList, ",");//将集合拼成字符串 用逗号相连
        List<Integer> userNumberList = new ArrayList<>();
        List<Integer> userNumberAllList = new ArrayList<>();
        Integer userNumberAll = 0;
        for (LocalDate date : dateList) {
            //找到开始日期具体时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //找到结束日期具体时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询营业额
            Map map = new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            //找出每一天的用户数目
            Integer userCount = userMapper.sumByMap(map);
            userCount = userCount == null ? 0 : userCount;
            userNumberList.add(userCount);
            //对每天的用户数目累加
            userNumberAll += userCount;
            userNumberAllList.add(userNumberAll);
        }

        return UserReportVO.builder()
                .dateList(listToString)
                .newUserList(StringUtils.join(userNumberList,","))
                .totalUserList(StringUtils.join(userNumberAllList,","))
                .build();
    }
}