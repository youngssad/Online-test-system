package cn.com.OnlineTest.controller;

import cn.com.OnlineTest.dao.*;
import cn.com.OnlineTest.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

@Controller
@RequestMapping("/")
public class ExamController  {
    HttpSession session;
    @Autowired
    ExamDao examDao;
    @Autowired
    PaperDao paperDao;
    @Autowired
    StudentexamDao studentexamDao;
    @Autowired
    StudentsubjectDao studentsubjectDao;
    @Autowired
    SubjectDao subjectDao;
    @Autowired
    CourseDao courseDao;
    @RequestMapping("/examList")
    private  String examList(Model model, HttpServletRequest request){
        HttpSession session = request.getSession(true);
        Integer classid= (Integer) session.getAttribute("classid");
        List<Exam> exams = examDao.finbyclassid(classid);
        for (Exam exam:exams){
            Course byCno = courseDao.findByCno(exam.getCno());
            exam.setCourse(byCno);
        }
        model.addAttribute("examslenth",exams.size());
        model.addAttribute("exams",exams);
        return "/student/examList";
    }
    @ResponseBody
    @RequestMapping("/findExamByEid")
    private Exam findExamByEid(@RequestBody Exam exams){
        Exam exam = examDao.findByEid(exams.getEid());
        if (exam!= null) {
            return exam;
        } else {
            return null;
        }
    }
    @RequestMapping("/paper")
    private String paper(Integer eid,Model model,HttpServletRequest request ){
        List<Paper> Single = paperDao.finbytype(eid, 1);
        Integer cont = Single.size();
        request.getSession().setAttribute("single",Single);
       model.addAttribute("single",Single);
        model.addAttribute("cont",cont);
        List<Paper> Multiple = paperDao.finbytype(eid, 2);
        Integer cont1 =Multiple.size();
        request.getSession().setAttribute("multiple",Multiple);
         model.addAttribute("multiple",Multiple);
        model.addAttribute("cont1",cont1);
        Exam exam = examDao.findByEid(eid);
        model.addAttribute("exam",exam);
        return "student/papers";
    }
    @RequestMapping("/PaperScore")
    private String PaperScore(HttpServletRequest request,Model model){
        HttpSession session = request.getSession(true);
        Integer classid =(Integer)session.getAttribute("classid") ;
        Integer userid =(Integer)session.getAttribute("userid");
        List<Paper> slist= (List<Paper>)session.getAttribute("single");
        Integer eid =Integer.parseInt(request.getParameter("eid"));
        Exam byEid = examDao.findByEid(eid);
        Integer singlescore = byEid.getSinglecore();
        String stuAnsArr[] = null;
        Integer score =0;
        for (int i = 0; i < slist.size(); ++i) {
            Paper paper = slist.get(i);
            stuAnsArr =request.getParameterValues(String.valueOf(paper.getSid()));
            if (stuAnsArr != null) {
                String studentkeys = "";
                for (int j = 0; j < stuAnsArr.length; j++) {
                    studentkeys += stuAnsArr[j];
                }
                if (studentkeys.equalsIgnoreCase(paper.getSkey())) {
                    score =score+singlescore;
                }else {
                }
            }else {
                System.out.println("submit fail！");
            }
        }
        Integer zscore =slist.size()*singlescore;
        String pname = request.getParameter("pname");
        String tjtime = request.getParameter("tjtime");
        model.addAttribute("score",score);
        Studentexam studentexam = new Studentexam();
        studentexam.setEid(eid);
        studentexam.setPname(pname);
        studentexam.setUserid(userid);
        studentexam.setClassid(classid);
        studentexam.setZscore(zscore);
        studentexam.setScore(score);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        try {
            ts = Timestamp.valueOf(tjtime);
            System.out.println(ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        studentexam.setTjtime(ts);
        studentexamDao.save(studentexam);
       Integer seid= studentexam.getSeid();
        for (int i = 0; i < slist.size(); ++i) {
            Paper paper = slist.get(i);
            stuAnsArr =request.getParameterValues(String.valueOf(paper.getSid()));
            if (stuAnsArr != null) {
                String studentkeys = "";
                for (int j = 0; j < stuAnsArr.length; j++) {
                    studentkeys += stuAnsArr[j];

                    Studentsubject studentsubject = new Studentsubject();
                    studentsubject.setSeid(seid);
                    studentsubject.setUserid(userid);
                    studentsubject.setEid(eid);
                    studentsubject.setSid(paper.getSid());
                    studentsubject.setStudentkey(studentkeys);
                    studentsubjectDao.save(studentsubject);
                }
            }
        }
        return "student/paperScore";
    }
    @ResponseBody
    @RequestMapping("/findOneStuExam")
    private Studentexam findOneStuExam(@RequestBody Exam exam,HttpServletRequest request){
        HttpSession session = request.getSession(true);
        Integer userid= (Integer) session.getAttribute("userid");
        Studentexam studentexam = studentexamDao.findByOne(userid,exam.getEid());
        return studentexam;
    }
    @RequestMapping("/findAllStuPaper")
    private String findAllStuPaper(Model model,HttpServletRequest request){
        HttpSession session = request.getSession(true);
        Integer userid= (Integer) session.getAttribute("userid");
        List<Studentexam> stuexamlist = studentexamDao.findByUserid(userid);
        model.addAttribute("stuexamlist",stuexamlist);
        return "student/stuPaperList";
    }
    @RequestMapping("/stuPaper")
    private String stuPaper(Integer seid,HttpServletRequest request,Model model){
        HttpSession session = request.getSession(true);
        Integer userid= (Integer) session.getAttribute("userid");
        List<Studentsubject> stukeys = studentsubjectDao.findBySeid(userid, seid);
        for (Studentsubject studentsubject :stukeys){
            Subject bySid = subjectDao.findBySid(studentsubject.getSid());
            Exam byEid = examDao.findByEid(studentsubject.getEid());
            model.addAttribute("exam",byEid);
            studentsubject.setSubject(bySid);
        }
        model.addAttribute("stukeys",stukeys);
        return "student/stuPaper";
    }
    @ResponseBody
    @RequestMapping("/findBySeid")
    private Studentexam findBySeid(@RequestBody Studentexam exams){
        Studentexam stexam = studentexamDao.findByseid(exams.getSeid());
        if (stexam!= null) {
            return stexam;
        } else {
            return null;
        }
    }
    @ResponseBody
    @RequestMapping("/findByPname")
    private Exam findByPname(@RequestBody Exam exams){
        Exam exam = examDao.findByPname(exams.getPname());
        if (exam!= null) {
            return exam;
        } else {
            return null;
        }
    }
}