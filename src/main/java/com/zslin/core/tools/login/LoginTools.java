package com.zslin.core.tools.login;

import com.zslin.core.dao.IAdminMenuDao;
import com.zslin.core.model.AdminMenu;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsl on 2018/7/13.
 */
@Component
public class LoginTools {

    @Autowired
    private IAdminMenuDao menuDao;

    public LoginDto buildAuthMenus(Integer userId) {
        List<AdminMenu> rootMenuList = menuDao.findRootByUser(userId, SimpleSortBuilder.generateSort("orderNo_a"));
        List<MenuDto> navMenuDtoList = new ArrayList<>();
        for(AdminMenu rootMenu : rootMenuList) { //root
            List<AdminMenu> secondMenuList = menuDao.findByUser(userId, rootMenu.getSn(), SimpleSortBuilder.generateSort("orderNo_a"));
            navMenuDtoList.add(new MenuDto(rootMenu, secondMenuList));
        }

        List<AdminMenu> authMenuList = menuDao.findAuthMenuByUser(userId);

        return new LoginDto(navMenuDtoList, authMenuList);
    }
}
