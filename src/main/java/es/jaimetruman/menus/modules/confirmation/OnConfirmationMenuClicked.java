package es.jaimetruman.menus.modules.confirmation;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.MenuService;
import es.jaimetruman.menus.OnMenuClicked;
import org.bukkit.entity.Player;

public final class OnConfirmationMenuClicked implements OnMenuClicked {
    private final MenuService menuService;

    public OnConfirmationMenuClicked() {
        this.menuService = ClassMapperInstanceProvider.MENU_SERVICE;
    }

    @Override
    public void on(Player player, Menu menu, int itemNumClicked) {
        if(hasClickedConfirmationItems(menu, itemNumClicked)){
            if(menu.getConfiguration().getConfirmationConfiguration().isCloseOnAction())
                this.menuService.close(player);
        }
    }

    private boolean hasClickedConfirmationItems(Menu menu, int itemNumClicked){
        return menu.getConfiguration().isConfirmation() && (menu.getConfiguration().getConfirmationConfiguration().getCancel().getItemNum() == itemNumClicked ||
                menu.getConfiguration().getConfirmationConfiguration().getAccept().getItemNum() == itemNumClicked);
    }
}
