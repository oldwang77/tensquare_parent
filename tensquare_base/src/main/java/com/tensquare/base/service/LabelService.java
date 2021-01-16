package com.tensquare.base.service;


import com.tensquare.base.dao.LabelDao;
import com.tensquare.base.pojo.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LabelService {
    @Autowired
    private LabelDao labelDao;
    @Autowired
    private IdWorker idWorker;

    public List<Label> findAll() {
        return labelDao.findAll();
    }

    public Label findById(String id) {
        return labelDao.findById(id).get();
    }

    public void save(Label label) {
        label.setId(idWorker.nextId() + "");
        labelDao.save(label);
    }

    // label对象里面有ID就更新，没有ID就保存
    public void update(Label label) {
        labelDao.save(label);
    }

    public void deleteById(String id) {
        labelDao.deleteById(id);
    }


    // 郑重写法就是固定的格式，写一份复制粘贴就好了
    public List<Label> findSearch(Label label) {
        // finaAll传入一个接口对象
        return labelDao.findAll(new Specification<Label>() {
            /**
             * @param root  根对象，也就是条件封装到哪个对象中。where 类名=label.getId
             * @param query 封装的查询关键字 比如 group by order by等
             * @param cb    用来封装条件对象 若直接返回null,表示不需要任何条件
             * @return
             */
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                //new 一个 list 集合，来存放所有的条件
                List<Predicate> list = new ArrayList<>();
                if (label.getLabelname() != null && !"".equals(label.getLabelname())) {
                    // root.get拿的是泛型中的属性名,Label中的labelname
                    Predicate predicate = cb.like(root.get("labelname").as(String.class), "%" + label.getLabelname() + "%");  //where labelname like "%小明%"
                    list.add(predicate);
                }
                if (label.getState() != null && !"".equals(label.getState())) {
                    Predicate predicate = cb.equal(root.get("state").as(String.class), label.getState());  //where state="1"
                    list.add(predicate);
                }

                //new一个数组作为最终返回值的条件
                Predicate[] parr = new Predicate[list.size()];
                //把list直接转成数组
                parr = list.toArray(parr);
                return cb.and(parr);            // where labelname like = '%小明' and state = "1"
            }
        });
    }

    public Page<Label> pageQuery(Label label, int page, int size) {
        // 分装一个分页对象
        Pageable pageable = PageRequest.of(page - 1, size);
        // 和上面相比，多了一个参数，pageable,我们封装一下就可以了
        return labelDao.findAll(new Specification<Label>() {
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<>();
                if (label.getLabelname() != null && !"".equals(label.getState())) {
                    Predicate predicate = cb.like(root.get("labelname").as(String.class), "%" + label.getLabelname());
                    list.add(predicate);
                }
                if (label.getState() != null && !"".equals(label.getState())) {
                    Predicate predicate = cb.equal(root.get("state").as(String.class), label.getState());
                    list.add(predicate);
                }
                Predicate[] parr = new Predicate[list.size()];
                parr = list.toArray(parr);
                return cb.and(parr);
            }
        }, pageable);
    }
}





