package com.bgav.javarushtest.controller;

import com.bgav.javarushtest.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.bgav.javarushtest.service.BookService;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер.
 */

@Controller
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private static int id = 0;
    private BookService bookService;

    @Autowired(required = true)
    @Qualifier(value = "bookService")
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Регистрация новой книги.
     * <p>
     * Если книги нет (id == 0), создаём новую. Если есть, обновляем то, что есть.
     *
     * @param book
     * @return редирект в root
     */
    @RequestMapping(value = "books/add", method = RequestMethod.POST)
    public String addBook(@ModelAttribute("book") Book book) {
        if (book.getId() == 0) this.bookService.addBook(book);
        else this.bookService.updateBook(book);
        return "redirect:/";
    }

    /**
     * Удаление книги по ID.
     *
     * @param id
     * @return редирект в root
     */
    @RequestMapping("/remove/{id}")
    public String removeBook(@PathVariable("id") int id) {
        this.bookService.removeBook(id);
        return "redirect:/";
    }

    /**
     * Изменение данных книги.
     *
     * Метод получает id и экземпляр модели.
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/edit/{id}")
    public String editBook(@PathVariable("id") int id, Model model) {
        logger.debug("BookController", "editBook");
        BookController.id = this.bookService.getBookById(id).getId();
//        model.addAttribute("book", this.bookService.getBookById(id));
        model.addAttribute("listBooks", this.bookService.listBooks());
        return "redirect:/";
    }

    @RequestMapping("bookdata{id}")
    public String bookData(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", this.bookService.getBookById(id));
        return "redirect:/";
    }

    /**
     * Метод пейджинга.
     *
     * Он срабатывает при запросе "/". Далее происходит следующее:
     * В метод передаётся номер страницы и поисковый запрос в String, если он есть.
     * Далее — если запрос есть — список наполняется только объектами Book, имена которых содержат строку запроса.
     * Если его нет — копируется основной список.
     *
     * Список отправляется в модель, которую и возвращает метод.
     *
     * @param page — номер страницы.
     * @param bookName — поисковый запрос.
     * @return модель, аттрибуты которой участвуют при наполнении списка.
     */
    @RequestMapping(value = "/")
    public ModelAndView listOfBooks(@RequestParam(required = false) Integer page, @RequestParam(required = false) String bookName) {
        ModelAndView modelAndView = new ModelAndView("index");
        if (id != 0) {
            modelAndView.addObject("book", this.bookService.getBookById(id));
            id = 0;
        } else {
            modelAndView.addObject("book", new Book());
        }
        List<Book> books = null;
        if (bookName == null || bookName.length() < 3) {
            books = this.bookService.listBooks();
        } else {
            List<Book> tempBooks = this.bookService.listBooks();
            books = new ArrayList<Book>();
            for (Book tempBook : tempBooks)
                if (tempBook.getTitle().toLowerCase().contains(bookName.toLowerCase()))
                    books.add(tempBook);
        }
        PagedListHolder<Book> pagedListHolder = new PagedListHolder<Book>(books);
        pagedListHolder.setPageSize(10);
        modelAndView.addObject("maxPages", pagedListHolder.getPageCount());

        if (page == null || page < 1 || page > pagedListHolder.getPageCount()) {
            page = 1;
        }

        modelAndView.addObject("page", page);
        if (page == null || page < 1 || page > pagedListHolder.getPageCount()) {
            pagedListHolder.setPage(0);
            modelAndView.addObject("listBooks", pagedListHolder.getPageList());
        } else if (page <= pagedListHolder.getPageCount()) {
            pagedListHolder.setPage(page - 1);
            modelAndView.addObject("listBooks", pagedListHolder.getPageList());
        }
        return modelAndView;
    }
}
