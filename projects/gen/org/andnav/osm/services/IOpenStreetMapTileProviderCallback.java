/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/andreaswalz/Downloads/osmdroid/osmdroid-read-only/OpenStreetMapViewer/src/org/andnav/osm/services/IOpenStreetMapTileProviderCallback.aidl
 */
package org.andnav.osm.services;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import android.graphics.Bitmap;
public interface IOpenStreetMapTileProviderCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.andnav.osm.services.IOpenStreetMapTileProviderCallback
{
private static final java.lang.String DESCRIPTOR = "org.andnav.osm.services.IOpenStreetMapTileProviderCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IOpenStreetMapTileProviderCallback interface,
 * generating a proxy if needed.
 */
public static org.andnav.osm.services.IOpenStreetMapTileProviderCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.andnav.osm.services.IOpenStreetMapTileProviderCallback))) {
return ((org.andnav.osm.services.IOpenStreetMapTileProviderCallback)iin);
}
return new org.andnav.osm.services.IOpenStreetMapTileProviderCallback.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_mapTileLoaded:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
android.graphics.Bitmap _arg4;
if ((0!=data.readInt())) {
_arg4 = android.graphics.Bitmap.CREATOR.createFromParcel(data);
}
else {
_arg4 = null;
}
this.mapTileLoaded(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_mapTileFailed:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
this.mapTileFailed(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.andnav.osm.services.IOpenStreetMapTileProviderCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void mapTileLoaded(int rendererID, int zoomLevel, int tileX, int tileY, android.graphics.Bitmap aImage) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(rendererID);
_data.writeInt(zoomLevel);
_data.writeInt(tileX);
_data.writeInt(tileY);
if ((aImage!=null)) {
_data.writeInt(1);
aImage.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_mapTileLoaded, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void mapTileFailed(int rendererID, int zoomLevel, int tileX, int tileY) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(rendererID);
_data.writeInt(zoomLevel);
_data.writeInt(tileX);
_data.writeInt(tileY);
mRemote.transact(Stub.TRANSACTION_mapTileFailed, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_mapTileLoaded = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_mapTileFailed = (IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void mapTileLoaded(int rendererID, int zoomLevel, int tileX, int tileY, android.graphics.Bitmap aImage) throws android.os.RemoteException;
public void mapTileFailed(int rendererID, int zoomLevel, int tileX, int tileY) throws android.os.RemoteException;
}
