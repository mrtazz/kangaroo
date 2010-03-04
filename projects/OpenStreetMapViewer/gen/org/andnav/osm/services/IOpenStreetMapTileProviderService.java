/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/andreaswalz/Documents/Uni/Informatik/Teamprojekt - WS0910/repository/master/kangaroo/projects/OpenStreetMapViewer/src/org/andnav/osm/services/IOpenStreetMapTileProviderService.aidl
 */
package org.andnav.osm.services;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
public interface IOpenStreetMapTileProviderService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.andnav.osm.services.IOpenStreetMapTileProviderService
{
private static final java.lang.String DESCRIPTOR = "org.andnav.osm.services.IOpenStreetMapTileProviderService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IOpenStreetMapTileProviderService interface,
 * generating a proxy if needed.
 */
public static org.andnav.osm.services.IOpenStreetMapTileProviderService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.andnav.osm.services.IOpenStreetMapTileProviderService))) {
return ((org.andnav.osm.services.IOpenStreetMapTileProviderService)iin);
}
return new org.andnav.osm.services.IOpenStreetMapTileProviderService.Stub.Proxy(obj);
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
case TRANSACTION_getTileProviders:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.getTileProviders();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_getMapTile:
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
org.andnav.osm.services.IOpenStreetMapTileProviderCallback _arg4;
_arg4 = org.andnav.osm.services.IOpenStreetMapTileProviderCallback.Stub.asInterface(data.readStrongBinder());
this.getMapTile(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.andnav.osm.services.IOpenStreetMapTileProviderService
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
public java.lang.String[] getTileProviders() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getTileProviders, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void getMapTile(int rendererID, int zoomLevel, int tileX, int tileY, org.andnav.osm.services.IOpenStreetMapTileProviderCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(rendererID);
_data.writeInt(zoomLevel);
_data.writeInt(tileX);
_data.writeInt(tileY);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_getMapTile, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getTileProviders = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getMapTile = (IBinder.FIRST_CALL_TRANSACTION + 1);
}
public java.lang.String[] getTileProviders() throws android.os.RemoteException;
public void getMapTile(int rendererID, int zoomLevel, int tileX, int tileY, org.andnav.osm.services.IOpenStreetMapTileProviderCallback callback) throws android.os.RemoteException;
}
