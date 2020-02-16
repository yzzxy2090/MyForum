package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer pageIndex, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount, pageIndex, size);

        //处理访问页面越界
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        if (pageIndex > paginationDTO.getTotalPage()) {
            pageIndex = paginationDTO.getTotalPage();
        }

        /**
         * select * from question limit offset, size;
         * 从question数据表中查出从offset开始的size条记录
         * offset=size*(page-1)
         *
         * 如，第1页想要展示第1到第5个问题共5条记录
         * select * from question limit 0, 5;
         */
        Integer offset = size * (pageIndex - 1);
        List<Question> questions = questionMapper.list(offset, size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        //去数据库中查到当前情况下需要展示的问题列表
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }
}
