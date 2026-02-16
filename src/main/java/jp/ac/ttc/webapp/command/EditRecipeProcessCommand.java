package jp.ac.ttc.webapp.command;

import jp.ac.ttc.webapp.bean.RecipeBean;
import jp.ac.ttc.webapp.connector.MySqlConnectionManager;
import jp.ac.ttc.webapp.context.RequestContext;
import jp.ac.ttc.webapp.context.ResponseContext;
import jp.ac.ttc.webapp.dao.AdminRecipeDao;

public class EditRecipeProcessCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        String idStr = reqc.getParameter("recipeId")[0];
        String instruction = reqc.getParameter("instructions")[0];
        String ingredientsJpJson = reqc.getParameter("ingredientsJpJson")[0];
        String ingredientsEnJson = reqc.getParameter("ingredientsEnJson")[0];
        String activeFlagStr = reqc.getParameter("activeFlag")[0];
        int id = Integer.parseInt(idStr);
        boolean activeFlag = Boolean.parseBoolean(activeFlagStr);

        System.out.println("activeFlg" + activeFlag);

        RecipeBean recipe = new RecipeBean();
        recipe.setRecipeId(id);
        recipe.setInstructions(instruction);
        recipe.setIngredientsEn(ingredientsEnJson);
        recipe.setIngredientsJp(ingredientsJpJson);
        recipe.setActiveFlag(activeFlag);

        MySqlConnectionManager.getInstance().beginTransaction();
        AdminRecipeDao dao = new AdminRecipeDao();
        dao.editRecipe(recipe);
        MySqlConnectionManager.getInstance().commit();
        MySqlConnectionManager.getInstance().closeConnection();

        resc.setRedirect(true);
        resc.setTarget("/spiceEC/admin/editRecipe?recipeId=" + idStr );
        return resc;
    }
}
