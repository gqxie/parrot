package indi.gqxie.parrot.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import indi.gqxie.parrot.system.entity.TErrorMessage;
import indi.gqxie.parrot.system.mapper.TErrorMessageMapper;
import indi.gqxie.parrot.system.service.parrot.TErrorMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xieguoqiang
 * @since 2018-08-07
 */
@Controller
@RequestMapping("/errorMessage")
public class TErrorMessageController
{
    @Autowired
    private TErrorMessageService errorMessageService;

    @Autowired
    TErrorMessageMapper mapper;

    @GetMapping("/list")
    public ResponseEntity<List<TErrorMessage>> getErrorMessage()
    {
        return ResponseEntity.ok(errorMessageService.getErrorMessage());
    }

    @GetMapping("/status")
    public ResponseEntity<Integer> getStatusByCarId()
    {
        return ResponseEntity.ok(errorMessageService.getStatusByCarId(1));
    }

    @GetMapping("/save")
    public ResponseEntity<Integer> save(TErrorMessage message)
    {
        return ResponseEntity.ok(errorMessageService.save(message));
    }

    @GetMapping("/pageQuery")
    public ResponseEntity<List<TErrorMessage>> testErrorMessage(Integer id)
    {
        List<TErrorMessage> messageList = mapper
                .selectPage(new Page<TErrorMessage>(0, 10), new EntityWrapper<TErrorMessage>().eq("id", id));
        return ResponseEntity.ok(messageList);
    }
}

