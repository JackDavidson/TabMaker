package wolf.games.mobile.tools;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseSineInOut;

//import com.badlogic.gdx.physics.box2d.Body;

import android.content.Context;
import android.util.Log;

public class ManageableSprite {
	
	//The activity making use of spriteObjet and SpriteManager
	protected SimpleBaseGameActivity mActivity;
	//The spriteObjectManager - manages these sprites
	protected SpriteManager mManager;
	//a texture region just for this one sprite. has to fit into the texture atlas with other sprites, which is held by our manager.
	protected TiledTextureRegion mTiledTextureRegion;
	protected TextureRegion mTextureRegion;
	
	protected Sprite BaseSprite;
	protected boolean isReadyForUse = false;
	protected int pTextureX;
	protected int pTextureY;
	
	private Runnable mOnTouchRunnable = null;
	public TouchEvent mSceneTouchEvent;
	
	//this does not complete sprite set up, as we do not learn if we are making an animated sprite or a static sprite untill makeNewAnimated... or makeNewSprite... is run.
	ManageableSprite(SpriteManager mManager, SimpleBaseGameActivity mActivity)
	{
		this.mActivity = mActivity;
		this.mManager = mManager;
	}
	
	//this actually completes the sprite set up. this is for an animated sprite.
	ManageableSprite(TiledTextureRegion mTiledTextureRegion, float pXLoc, float pYLoc, SpriteManager mManager, SimpleBaseGameActivity mActivity)
	{
		this.mActivity = mActivity;
		this.mManager = mManager;
		final AnimatedSprite tSprite;
		this.mTiledTextureRegion = mTiledTextureRegion;
		tSprite = new AnimatedSprite(pXLoc, pYLoc, this.mTiledTextureRegion, mActivity.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					ManageableSprite.this.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalY, pTouchAreaLocalY, this);
				return true;
			}
		};
		tSprite.setPosition(pXLoc - (tSprite.getWidth()/2), pYLoc - (tSprite.getHeight()/2));
		tSprite.setRotationCenter((tSprite.getWidth()/2), (tSprite.getHeight()/2));
		BaseSprite = tSprite;
		isReadyForUse = true;
	}
	//complete sprite setup. for a static sprite.
	ManageableSprite(TextureRegion mTextureRegion, float pXLoc, float pYLoc, SpriteManager mManager, SimpleBaseGameActivity mActivity)
	{
		this.mActivity = mActivity;
		this.mManager = mManager;
		final Sprite tSprite;
		this.mTextureRegion = mTextureRegion;
		tSprite = new Sprite(pXLoc, pYLoc, this.mTextureRegion, mActivity.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					ManageableSprite.this.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalY, pTouchAreaLocalY, this);
				return true;
			}
		};
		tSprite.setPosition(pXLoc - (tSprite.getWidth()/2), pYLoc - (tSprite.getHeight()/2));
		tSprite.setRotationCenter((tSprite.getWidth()/2), (tSprite.getHeight()/2));
		BaseSprite = tSprite;
		isReadyForUse = true;
	}
	
	public SimpleBaseGameActivity getActivity()
	{
		return this.mActivity;
	}
	
	public SpriteManager getManager()
	{
		return this.mManager;
	}
	
	
	//this sets up the sprite. It doesn't actually make anything new. returns its self.
	public ManageableSprite setupAsAnimatedSprite(String pAssetpath, float pXLoc, float pYLoc, int pTextureX, int pTextureY, int pTileColumns, int pTileRows)
	{	
		final AnimatedSprite tSprite;
		this.mTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mManager.mBitmapTextureAtlas, mActivity, pAssetpath, pTextureX, pTextureY, pTileColumns, pTileRows); // 64x32
		
		mManager.mBitmapTextureAtlas.load();
		
		tSprite = new AnimatedSprite(pXLoc, pYLoc, this.mTiledTextureRegion, mActivity.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					ManageableSprite.this.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalY, pTouchAreaLocalY, this);
				return true;
			}
		};
		
		tSprite.setPosition(pXLoc - (tSprite.getWidth()/2), pYLoc - (tSprite.getHeight()/2));
		tSprite.setRotationCenter((tSprite.getWidth()/2), (tSprite.getHeight()/2));
		BaseSprite = tSprite;
		
		isReadyForUse = true;
		
		return this;
	}
	
	public ManageableSprite animate (int speed)
	{
		if (BaseSprite instanceof AnimatedSprite)
		{
			((AnimatedSprite) BaseSprite).animate(200);
		}
		
		return this;
		//otherwise, do nothing
	}
	
	public ManageableSprite attachChild()
	{
		mManager.mScene.attachChild(BaseSprite);
		return this;
	}
	
	public ManageableSprite attachAsChildTo(Scene mScene){
		mScene.attachChild(BaseSprite);
		return this;
	}


	public TiledTextureRegion getTiledAssetPath() {
		return mTiledTextureRegion;
	}
	public TextureRegion getTextureRegion() {
		return mTextureRegion;
	}
	
	public Sprite getSprite()
	{
		return BaseSprite;
	}


	public ManageableSprite setupAsStaticSprite(String pAssetpath, float pXLoc, float pYLoc, int pTextureX, int pTextureY) {
		
		final Sprite tSprite;
		this.mTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mManager.mBitmapTextureAtlas, mActivity, pAssetpath, pTextureX, pTextureY); // 64x32
		
		mManager.mBitmapTextureAtlas.load();
		
		tSprite = new Sprite(pXLoc, pYLoc, this.mTextureRegion, mActivity.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
					ManageableSprite.this.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalY, pTouchAreaLocalY, this);
				return true;
			}
		};
		
		tSprite.setPosition(pXLoc - (tSprite.getWidth()/2), pYLoc - (tSprite.getHeight()/2));
		tSprite.setRotationCenter((tSprite.getWidth()/2), (tSprite.getHeight()/2));
		BaseSprite = tSprite;
		
		isReadyForUse = true;
		
		return this;
	}


	public ManageableSprite setRotation(int rotationAmount) {
		getSprite().setRotation(rotationAmount);
		return this;
	}
	
	public ManageableSprite addPath() {
		final Path path = new Path(5).to(10, 10).to(10, 74).to(58,74).to(58, 10).to(10, 10);

		/* Add the proper animation when a waypoint of the path is passed. */
		BaseSprite.registerEntityModifier(new LoopEntityModifier(new PathModifier(30, path, null, new IPathModifierListener() {
			@Override
			public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
				Debug.d("onPathStarted");
			}

			@Override
			public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
				Debug.d("onPathWaypointStarted:  " + pWaypointIndex);
				switch(pWaypointIndex) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
				}
			}

			@Override
			public void onPathWaypointFinished(PathModifier pPathModifier,
					IEntity pEntity, int pWaypointIndex) {
				
			}

			@Override
			public void onPathFinished(PathModifier pPathModifier,
					IEntity pEntity) {
				
			}
		}, EaseSineInOut.getInstance())));
		return this;
	}
	
	public ManageableSprite addLinearPath(int sX, int sY, int eX, int eY, int pDuration) {
		final float halfWidth = getSprite().getWidth()/2;
		final float halfHeight = getSprite().getHeight()/2;
		
		final Path path = new Path(3).to(sX - halfWidth, sY - halfHeight).to(eX - halfWidth, eY - halfHeight).to(sX - halfWidth, sY - halfHeight);

		/* Add the proper animation when a waypoint of the path is passed. */
		BaseSprite.registerEntityModifier(new LoopEntityModifier(new PathModifier(pDuration, path, null, new IPathModifierListener() {
			@Override
			public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
				Debug.d("onPathStarted");
			}

			@Override
			public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
				Debug.d("onPathWaypointStarted:  " + pWaypointIndex);
				switch(pWaypointIndex) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
				}
			}

			@Override
			public void onPathWaypointFinished(PathModifier pPathModifier,
					IEntity pEntity, int pWaypointIndex) {
				
			}

			@Override
			public void onPathFinished(PathModifier pPathModifier,
					IEntity pEntity) {
				
			}
		}, EaseSineInOut.getInstance())));
		return this;		
	}
	
	/*public void attachBody(Body mBody)
	{
		connectedBody = mBody;
	}*/

	
	public void setOnTouchRunnable(Runnable mRunnable)
	{
		mOnTouchRunnable = mRunnable;
	}
	
	private boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY, Sprite pSprite)
	{
		if (mOnTouchRunnable != null)
		{
			mSceneTouchEvent = pSceneTouchEvent;
			mOnTouchRunnable.run();
		}
		return true;
	}
	
	public void setPosition(float pXLoc, float pYLoc){
		BaseSprite.setPosition(pXLoc - (BaseSprite.getWidth()/2), pYLoc - (BaseSprite.getHeight()/2));
	}
	
	public void registerTouchArea(Runnable mRunnable) {
		
		mOnTouchRunnable = mRunnable;
		mManager.mScene.registerTouchArea(getSprite());
		
	}
	public void unregisterTouchArea() {
		mManager.mScene.unregisterTouchArea(getSprite());
		
	}

	public TouchEvent getTouchEvent() {
		return mSceneTouchEvent;
	}
	public float getTLPositionX() {
		return (BaseSprite.getX());
	}
	public float getTLPositionY() {
		return (BaseSprite.getY());
	}

	public float getPositionX() {
		return (BaseSprite.getX() + BaseSprite.getWidth()/2);
	}

	public float getPositionY() {
		return (BaseSprite.getY() + BaseSprite.getHeight()/2);
	}

	public float getRotation() {
		return this.getSprite().getRotation();
	}

	public void detachSelf() {
		this.getSprite().detachSelf();
		
	}
}
