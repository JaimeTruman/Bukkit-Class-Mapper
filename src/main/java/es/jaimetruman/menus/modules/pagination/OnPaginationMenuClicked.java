package es.jaimetruman.menus.modules.pagination;

import es.jaimetruman._shared.utils.ClassMapperInstanceProvider;
import es.jaimetruman.menus.Menu;
import es.jaimetruman.menus.OnMenuClicked;
import org.bukkit.entity.Player;

public final class OnPaginationMenuClicked implements OnMenuClicked {
    private final PaginationService paginationService;

    public OnPaginationMenuClicked() {
        this.paginationService = ClassMapperInstanceProvider.PAGINATION_SERVICE;
    }

    @Override
    public void on(Player player, Menu menu, int itemNumClicked) {
        if(hasClickedPaginationsItems(menu, itemNumClicked)){
            performPaginationAction(player, menu, itemNumClicked);
        }
    }

    private void performPaginationAction(Player player, Menu menu, int itemNumClicked) {
        if(itemNumClicked == menu.getConfiguration().getPaginationConfiguration().getBackward().getItemNum())
            paginationService.goBackward(player, menu);
        else
            paginationService.goForward(player, menu);
    }

    private boolean hasClickedPaginationsItems(Menu menu, int itemNumClicked) {
        return menu.getConfiguration().isPaginated() && (menu.getConfiguration().getPaginationConfiguration().getBackward().getItemNum() == itemNumClicked ||
                menu.getConfiguration().getPaginationConfiguration().getForward().getItemNum() == itemNumClicked);
    }

}
