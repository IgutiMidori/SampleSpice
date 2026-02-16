package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminRecipeDao;

public class EditRecipeCommand extends AbstractCommand {
    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String recipeIdStr = reqc.getParameter("recipeId")[0];
         
        AdminRecipeDao dao = new AdminRecipeDao();
        RecipeBean bean = dao.getResipeById(Integer.parseInt(recipeIdStr));
        
        resc.setResult(bean);
        resc.setTarget("editRecipe");
        return resc;
    }
}
