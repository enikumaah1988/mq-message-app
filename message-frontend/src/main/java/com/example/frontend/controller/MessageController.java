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
        int total = messageRepository.count("");
        List<Map<String, Object>> messages = messageRepository.findAllWithSortAndPaging(
            offset, pageSize, "created_at", "DESC", null);
        log.info("メッセージを{}件取得しました", messages.size());
        return new PageImpl<>(messages, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "created_at")), total);
    }

    @GetMapping
    public String index(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "created_at") String sortField,
                       @RequestParam(defaultValue = "desc") String sortDirection,
                       @RequestParam(required = false) String keyword) {
        if (!PAGE_SIZES.contains(size)) {
            size = DEFAULT_PAGE_SIZE;
        }

        int totalMessages = messageRepository.count(keyword);
        int totalPages = (int) Math.ceil((double) totalMessages / size);
        int offset = page * size;

        List<Map<String, Object>> messages = messageRepository.findAllWithSortAndPaging(
            offset, size, sortField, sortDirection, keyword);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("messages", messages);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageSizes", PAGE_SIZES);
        model.addAttribute("selectedPageSize", size);
        return "index";
    }

    @GetMapping("/messages/refresh")
    public String refreshMessages(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int pageSize,
                                @RequestParam(defaultValue = "created_desc") String sort,
                                @RequestParam(required = false) String keyword,
                                Model model) {
        log.info("メッセージ一覧を更新します");
        try {
            if (!PAGE_SIZES.contains(pageSize)) {
                pageSize = DEFAULT_PAGE_SIZE;
            }

            // ソートフィールドとソート方向を決定
            String sortField;
            String sortDirection;
            switch (sort) {
                case "created_asc":
                    sortField = "created_at";
                    sortDirection = "ASC";
                    break;
                case "updated_desc":
                    sortField = "updated_at";
                    sortDirection = "DESC";
                    break;
                case "updated_asc":
                    sortField = "updated_at";
                    sortDirection = "ASC";
                    break;
                default: // created_desc
                    sortField = "created_at";
                    sortDirection = "DESC";
                    break;
            }

            int offset = page * pageSize;
            List<Map<String, Object>> messages = messageRepository.findAllWithSortAndPaging(offset, pageSize, sortField, sortDirection, keyword);
            int totalCount = messageRepository.count(keyword);
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);

            model.addAttribute("messages", messages);
            model.addAttribute("pageSizes", PAGE_SIZES);
            model.addAttribute("selectedPageSize", pageSize);
            model.addAttribute("sort", sort);
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
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
                              @RequestParam(defaultValue = "created_desc") String sort,
                              @RequestParam(defaultValue = "") String keyword,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            if (!PAGE_SIZES.contains(pageSize)) {
                pageSize = DEFAULT_PAGE_SIZE;
            }

            // 文字数バリデーション
            if (content != null && content.length() > 255) {
                redirectAttributes.addFlashAttribute("message", "メッセージは255文字以内で入力してください");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/?page=" + page + "&pageSize=" + pageSize + "&sort=" + sort + "&keyword=" + keyword;
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
                    return "redirect:/?page=0&pageSize=" + pageSize + "&sort=" + sort + "&keyword=" + keyword;

                case "UPDATE":
                    if (id != null && content != null) {
                        log.info("メッセージを更新します: id={}, content={}", id, content);
                        jmsService.sendMessage(Map.of(
                            "action", "UPDATE",
                            "data", Map.of("id", id, "content", content)
                        ));
                        redirectAttributes.addFlashAttribute("message", "メッセージを更新しました");
                        redirectAttributes.addFlashAttribute("messageType", "success");
                        return "redirect:/?page=" + page + "&pageSize=" + pageSize + "&sort=" + sort + "&keyword=" + keyword;
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
                        return "redirect:/?page=" + page + "&pageSize=" + pageSize + "&sort=" + sort + "&keyword=" + keyword;
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
                        model.addAttribute("sort", sort);
                        model.addAttribute("keyword", keyword);
                        return "index";
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("メッセージ処理に失敗しました: action={}, error={}", action, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "操作に失敗しました");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/?page=" + page + "&pageSize=" + pageSize + "&sort=" + sort + "&keyword=" + keyword;
        }

        return "redirect:/?page=" + page + "&pageSize=" + pageSize + "&sort=" + sort + "&keyword=" + keyword;
    }

    @PostMapping("/messages/bulk-delete")
    public String bulkDelete(@RequestParam String ids,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int pageSize,
                           @RequestParam(defaultValue = "created_desc") String sort,
                           @RequestParam(required = false) String keyword,
                           RedirectAttributes redirectAttributes) {
        try {
            String[] idArray = ids.split(",");
            log.info("複数のメッセージを削除します: ids={}", ids);
            
            for (String idStr : idArray) {
                Long id = Long.parseLong(idStr);
                jmsService.sendMessage(Map.of(
                    "action", "DELETE",
                    "data", Map.of("id", id)
                )); 
            }
            
            redirectAttributes.addFlashAttribute("message", 
                String.format("%d件のメッセージを削除しました", idArray.length));
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            log.error("メッセージの一括削除に失敗しました: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "メッセージの一括削除に失敗しました");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        
        return String.format("redirect:/?page=%d&pageSize=%d&sort=%s&keyword=%s", 
                           page, pageSize, sort, keyword != null ? keyword : "");
    }
}