package com.example.frontend.controller;

import com.example.frontend.repository.MessageRepository;
import com.example.frontend.service.JmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

@Controller
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private static final List<Integer> PAGE_SIZES = Arrays.asList(5, 10, 50, 100, 200);
    private static final int DEFAULT_PAGE_SIZE = 5;
    private final JmsService jmsService;
    private final MessageRepository messageRepository;

    public MessageController(JmsService jmsService, MessageRepository messageRepository) {
        this.jmsService = jmsService;
        this.messageRepository = messageRepository;
    }

    private Page<Map<String, Object>> getMessages(int page, int pageSize) {
        log.info("メッセージ一覧を取得します（ページ: {}, サイズ: {}）", page, pageSize);
        int offset = page * pageSize;
        int total = messageRepository.count();
        List<Map<String, Object>> messages = messageRepository.findAllOrderByCreatedAtDescWithPaging(offset, pageSize);
        log.info("メッセージを{}件取得しました", messages.size());
        return new PageImpl<>(messages, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "created_at")), total);
    }

    private void handleMessageOperation(Model model, String operation, String message, int page, int pageSize) {
        try {
            Thread.sleep(2000);
            model.addAttribute("messages", getMessages(page, pageSize));
            model.addAttribute("message", message);
            model.addAttribute("messageType", "success");
            model.addAttribute("pageSizes", PAGE_SIZES);
            model.addAttribute("selectedPageSize", pageSize);
        } catch (Exception e) {
            log.error("{}後の処理に失敗しました: {}", operation, e.getMessage(), e);
            model.addAttribute("message", "操作に失敗しました");
            model.addAttribute("messageType", "error");
        }
    }

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "5") int pageSize,
                       Model model) {
        model.addAttribute("title", "メッセージ管理システム");
        model.addAttribute("content", "index :: content");
        try {
            if (!PAGE_SIZES.contains(pageSize)) {
                pageSize = DEFAULT_PAGE_SIZE;
            }
            model.addAttribute("messages", getMessages(page, pageSize));
            model.addAttribute("pageSizes", PAGE_SIZES);
            model.addAttribute("selectedPageSize", pageSize);
            return "index";
        } catch (Exception e) {
            log.error("メッセージの取得に失敗しました: {}", e.getMessage(), e);
            model.addAttribute("message", "メッセージの取得に失敗しました");
            model.addAttribute("messageType", "error");
            return "index";
        }
    }

    @GetMapping("/messages/refresh")
    public String refreshMessages(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int pageSize,
                                Model model) {
        log.info("メッセージ一覧を更新します");
        try {
            if (!PAGE_SIZES.contains(pageSize)) {
                pageSize = DEFAULT_PAGE_SIZE;
            }
            model.addAttribute("messages", getMessages(page, pageSize));
            model.addAttribute("pageSizes", PAGE_SIZES);
            model.addAttribute("selectedPageSize", pageSize);
            return "index";
        } catch (Exception e) {
            log.error("メッセージ一覧の更新に失敗しました: {}", e.getMessage(), e);
            model.addAttribute("message", "メッセージ一覧の更新に失敗しました");
            model.addAttribute("messageType", "error");
            return "index";
        }
    }

    @PostMapping("/messages")
    public String handleMessage(@RequestParam String action,
                              @RequestParam(required = false) Long id,
                              @RequestParam(required = false) String content,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int pageSize,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            if (!PAGE_SIZES.contains(pageSize)) {
                pageSize = DEFAULT_PAGE_SIZE;
            }
            log.info("メッセージ処理を開始: action={}, id={}, content={}", action, id, content);
            switch (action) {
                case "CREATE":
                    log.info("新規メッセージを作成します: {}", content);
                    jmsService.sendMessage(Map.of(
                        "action", "CREATE",
                        "data", Map.of("content", content)
                    ));
                    redirectAttributes.addFlashAttribute("message", "メッセージを送信しました");
                    redirectAttributes.addFlashAttribute("messageType", "success");
                    return "redirect:/?page=0&pageSize=" + pageSize;

                case "UPDATE":
                    if (id != null && content != null) {
                        log.info("メッセージを更新します: id={}, content={}", id, content);
                        jmsService.sendMessage(Map.of(
                            "action", "UPDATE",
                            "data", Map.of("id", id, "content", content)
                        ));
                        redirectAttributes.addFlashAttribute("message", "メッセージを更新しました");
                        redirectAttributes.addFlashAttribute("messageType", "success");
                        return "redirect:/?page=" + page + "&pageSize=" + pageSize;
                    }
                    break;

                case "DELETE":
                    if (id != null) {
                        log.info("メッセージを削除します: id={}", id);
                        jmsService.sendMessage(Map.of(
                            "action", "DELETE",
                            "data", Map.of("id", id)
                        ));
                        redirectAttributes.addFlashAttribute("message", "メッセージを削除しました");
                        redirectAttributes.addFlashAttribute("messageType", "success");
                        return "redirect:/?page=" + page + "&pageSize=" + pageSize;
                    }
                    break;

                case "EDIT":
                    if (id != null) {
                        log.info("メッセージの編集を開始: id={}", id);
                        messageRepository.findById(id).ifPresent(message -> {
                            model.addAttribute("editMessage", message);
                        });
                        model.addAttribute("messages", getMessages(page, pageSize));
                        model.addAttribute("pageSizes", PAGE_SIZES);
                        model.addAttribute("selectedPageSize", pageSize);
                        return "base";
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("メッセージ処理に失敗しました: action={}, error={}", action, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "操作に失敗しました");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/?page=" + page + "&pageSize=" + pageSize;
        }

        return "redirect:/?page=" + page + "&pageSize=" + pageSize;
    }
}