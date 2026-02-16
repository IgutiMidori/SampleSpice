package jp.ac.ttc.webapp.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ttc.webapp.bean.PageBean;
import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminRecipeDao;

/**
 * 管理者用レシピ一覧を表示するためのexecuteを持つCommandクラス
 * @author suzki-takumi23
 * @since 2026-02-03
 */
public class ListAdminRecipesCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        Map<String, Object> result = new HashMap<>();

        int currentPage = 1;
        if(reqc.hasParameter("page")) {
            currentPage = Integer.parseInt(reqc.getParameter("page")[0]);
        }

        MySqlConnectionManager.getInstance().beginTransaction();
        AdminRecipeDao dao = new AdminRecipeDao();
        PageBean pageBean = new PageBean(currentPage, dao.getRecipesCount(), PageBean.RECIPE_PAGE_SIZE);

        List<RecipeBean> recipes = dao.getAllRecipe(PageBean.RECIPE_PAGE_SIZE, pageBean.getOffset());
        MySqlConnectionManager.getInstance().closeConnection();
        result.put("recipes", recipes);
        result.put("pageBean", pageBean);

        resc.setResult(result);
        resc.setTarget("adminRecipeList");
        return resc;
    }
}
