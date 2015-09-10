package wolf.games.mobile.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;

import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.content.Context;
import android.util.Log;

public class SpriteManager {
	
	protected BitmapTextureAtlas mBitmapTextureAtlas;
	private SimpleBaseGameActivity myActivity;
	protected Scene mScene;
	

	protected Map <Integer, ManageableSprite> spriteMap;
	protected int numSprites = 0;
		
	
	
	public SpriteManager(SimpleBaseGameActivity myActivity, Scene mScene)
	{
		spriteMap = new HashMap<Integer, ManageableSprite>(50);
		this.mScene = mScene;
		this.myActivity = myActivity;
		mBitmapTextureAtlas = new BitmapTextureAtlas(myActivity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
	}
	
	public ManageableSprite makeNewTiledSprite(String pAssetpath, float pXLoc, float pYLoc, int pTextureX, int pTextureY, int pTileColumns, int pTileRows){
		spriteMap.put(numSprites, new ManageableSprite(this, myActivity));
		spriteMap.get(numSprites).setupAsAnimatedSprite(pAssetpath, pXLoc, pYLoc, pTextureX, pTextureY, pTileColumns, pTileRows);
		numSprites++;

		
		return spriteMap.get(numSprites - 1);
	}
	
	public Scene getScene()
	{
		return this.mScene;
	}
	
	public ManageableSprite makeNewTiledSprite(int spriteCode, int pX, int pY){	
		return makeNewTiledSprite(spriteMap.get(spriteCode), pX, pY);
	}
	public ManageableSprite makeNewTiledSprite(ManageableSprite refSprite, float pX, float pY){
		spriteMap.put(numSprites, new ManageableSprite(refSprite.getTiledAssetPath(), pX, pY, refSprite.getManager(), refSprite.getActivity()));
		numSprites++;
		return spriteMap.get(numSprites - 1);
	}
	
	public ManageableSprite makeNewSprite(String pAssetpath, float pXLoc, float pYLoc, int pTextureX, int pTextureY){
		spriteMap.put(numSprites, new ManageableSprite(this, myActivity));
		spriteMap.get(numSprites).setupAsStaticSprite(pAssetpath, pXLoc, pYLoc, pTextureX, pTextureY);
		numSprites++;
		return spriteMap.get(numSprites - 1);
	}
	public ManageableSprite makeNewSprite(int spriteCode, int pX, int pY){	
		return makeNewSprite(spriteMap.get(spriteCode), pX, pY);
	}
	public ManageableSprite makeNewSprite(ManageableSprite refSprite, float pX, float pY){
		spriteMap.put(numSprites, new ManageableSprite(refSprite.getTextureRegion(), pX, pY, refSprite.getManager(), refSprite.getActivity()));
		numSprites++;
		return spriteMap.get(numSprites - 1);
	}
	public ManageableSprite makeNewSprite(ManageableSprite refSprite){
		spriteMap.put(numSprites, new ManageableSprite(refSprite.getTextureRegion(), refSprite.getPositionX(), refSprite.getPositionY(), refSprite.getManager(), refSprite.getActivity()));
		numSprites++;
		return spriteMap.get(numSprites - 1);
	}
	public Sprite getSprite(int spriteNum)
	{
		return spriteMap.get(spriteNum).getSprite();
	}
	
	public SimpleBaseGameActivity getContext()
	{
		return myActivity;
	}

}
