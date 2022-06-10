/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.glfw;

import javax.annotation.*;

import java.nio.*;

import org.lwjgl.*;

import org.lwjgl.system.*;

import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.JNI.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTMetalSurface.*;
import static org.lwjgl.vulkan.KHRAndroidSurface.*;

import org.lwjgl.vulkan.*;

/** Native bindings to the GLFW library's Vulkan functions. */
public class GLFWVulkan {

    protected GLFWVulkan() {
        throw new UnsupportedOperationException();
    }

    static {
        //invokeV(Functions.Init);


/*
        if (Platform.get() == Platform.MACOSX) {
            // Force GLFW to initialize Vulkan using the same library used by LWJGL.
            FunctionProvider fp = VK.getFunctionProvider();
            if (fp instanceof SharedLibrary) {
                String path = ((SharedLibrary)fp).getPath();
                if (path != null) {
                    try (MemoryStack stack = stackPush()) {
                        long _glfw_vulkan_library = apiGetFunctionAddress(GLFW.getLibrary(), "_glfw_vulkan_library");

                        memPutAddress(_glfw_vulkan_library, memAddress(stack.UTF8(path)));
                        glfwVulkanSupported();
                        memPutAddress(_glfw_vulkan_library, NULL);
                    }
                }
            }
        }
*/
    }

    // --- [ glfwVulkanSupported ] ---

    /**
     * Returns whether the Vulkan loader has been found. This check is performed by {@link GLFW#glfwInit Init}.
     * 
     * <p>The availability of a Vulkan loader does not by itself guarantee that window surface creation or even device creation is possible. Call
     * {@link #glfwGetRequiredInstanceExtensions GetRequiredInstanceExtensions} to check whether the extensions necessary for Vulkan surface creation are available and
     * {@link #glfwGetPhysicalDevicePresentationSupport GetPhysicalDevicePresentationSupport} to check whether a queue family of a physical device supports image presentation.</p>
     * 
     * <p>Possible errors include {@link GLFW#GLFW_NOT_INITIALIZED NOT_INITIALIZED}.</p>
     * 
     * <p>This function may be called from any thread.</p>
     *
     * @return {@link GLFW#GLFW_TRUE TRUE} if Vulkan is available, or {@link GLFW#GLFW_FALSE FALSE} otherwise
     *
     * @since version 3.2
     */
    @NativeType("int")
    public static boolean glfwVulkanSupported() {
        return true;
    }

    // --- [ glfwGetRequiredInstanceExtensions ] ---

    /**
     * Returns an array of names of Vulkan instance extensions required by GLFW for creating Vulkan surfaces for GLFW windows. If successful, the list will
     * always contain {@code VK_KHR_surface}, so if you don't require any additional extensions you can pass this list directly to the    {@link VkInstanceCreateInfo}
     * struct.
     * 
     * <p>If Vulkan is not available on the machine, this function returns {@code NULL} and generates a {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE} error. Call {@link #glfwVulkanSupported VulkanSupported} to check whether
     * Vulkan is available.</p>
     * 
     * <p>If Vulkan is available but no set of extensions allowing window surface creation was found, this function returns {@code NULL}. You may still use Vulkan for
     * off-screen rendering and compute work.</p>
     * 
     * <p>Additional extensions may be required by future versions of GLFW. You should check if any extensions you wish to enable are already in the returned
     * array, as it is an error to specify an extension more than once in the {@code VkInstanceCreateInfo} struct.</p>
     * 
     * <p>The returned array is allocated and freed by GLFW. You should not free it yourself. It is guaranteed to be valid only until the library is terminated.</p>
     * 
     * <p>This function may be called from any thread.</p>
     * 
     * <p>Possible errors include {@link GLFW#GLFW_NOT_INITIALIZED NOT_INITIALIZED} and {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE}.</p>
     *
     * @return an array of ASCII encoded extension names, or {@code NULL} if an error occurred
     *
     * @since version 3.2
     */
    @Nullable
    @NativeType("char const **")
    public static PointerBuffer glfwGetRequiredInstanceExtensions() {
        MemoryStack stack = MemoryStack.stackPush();
        String platformSurface;
        if (Platform.get() == Platform.MACOSX) {
            platformSurface = "VK_EXT_metal_surface";
        } else {
            platformSurface = "VK_KHR_android_surface";
        }
        return stack.pointers(stack.UTF8(KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME), stack.UTF8(platformSurface));
    }

    // --- [ glfwGetInstanceProcAddress ] ---

    /**
     * Returns the address of the specified Vulkan core or extension function for the specified instance. If instance is set to {@code NULL} it can return any
     * function exported from the Vulkan loader, including at least the following functions:
     * 
     * <ul>
     * <li>{@link VK10#vkEnumerateInstanceExtensionProperties}</li>
     * <li>{@link VK10#vkEnumerateInstanceLayerProperties}</li>
     * <li>{@link VK10#vkCreateInstance}</li>
     * <li>{@link VK10#vkGetInstanceProcAddr}</li>
     * </ul>
     * 
     * <p>If Vulkan is not available on the machine, this function returns {@code NULL} and generates a {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE} error. Call {@link #glfwVulkanSupported VulkanSupported} to check whether
     * Vulkan is available.</p>
     * 
     * <p>This function is equivalent to calling {@link VK10#vkGetInstanceProcAddr} with a platform-specific query of the Vulkan loader as a fallback.</p>
     * 
     * <p>Possible errors include {@link GLFW#GLFW_NOT_INITIALIZED NOT_INITIALIZED} and {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE}.</p>
     * 
     * <p>The returned function pointer is valid until the library is terminated.</p>
     * 
     * <p>This function may be called from any thread.</p>
     *
     * @param instance the Vulkan instance to query, or {@code NULL} to retrieve functions related to instance creation
     * @param procname the ASCII encoded name of the function
     *
     * @return the address of the function, or {@code NULL} if an error occurred
     *
     * @since version 3.2
     */
    @NativeType("GLFWvkproc")
    public static long glfwGetInstanceProcAddress(@Nullable VkInstance instance, @NativeType("char const *") ByteBuffer procname) {
        return VK10.vkGetInstanceProcAddr(instance, procname);
    }

    /**
     * Returns the address of the specified Vulkan core or extension function for the specified instance. If instance is set to {@code NULL} it can return any
     * function exported from the Vulkan loader, including at least the following functions:
     * 
     * <ul>
     * <li>{@link VK10#vkEnumerateInstanceExtensionProperties}</li>
     * <li>{@link VK10#vkEnumerateInstanceLayerProperties}</li>
     * <li>{@link VK10#vkCreateInstance}</li>
     * <li>{@link VK10#vkGetInstanceProcAddr}</li>
     * </ul>
     * 
     * <p>If Vulkan is not available on the machine, this function returns {@code NULL} and generates a {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE} error. Call {@link #glfwVulkanSupported VulkanSupported} to check whether
     * Vulkan is available.</p>
     * 
     * <p>This function is equivalent to calling {@link VK10#vkGetInstanceProcAddr} with a platform-specific query of the Vulkan loader as a fallback.</p>
     * 
     * <p>Possible errors include {@link GLFW#GLFW_NOT_INITIALIZED NOT_INITIALIZED} and {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE}.</p>
     * 
     * <p>The returned function pointer is valid until the library is terminated.</p>
     * 
     * <p>This function may be called from any thread.</p>
     *
     * @param instance the Vulkan instance to query, or {@code NULL} to retrieve functions related to instance creation
     * @param procname the ASCII encoded name of the function
     *
     * @return the address of the function, or {@code NULL} if an error occurred
     *
     * @since version 3.2
     */
    @NativeType("GLFWvkproc")
    public static long glfwGetInstanceProcAddress(@Nullable VkInstance instance, @NativeType("char const *") CharSequence procname) {
        return VK10.vkGetInstanceProcAddr(instance, procname);
    }

    // --- [ glfwGetPhysicalDevicePresentationSupport ] ---

    /**
     * Returns whether the specified queue family of the specified physical device supports presentation to the platform GLFW was built for.
     * 
     * <p>If Vulkan or the required window surface creation instance extensions are not available on the machine, or if the specified instance was not created
     * with the required extensions, this function returns {@link GLFW#GLFW_FALSE FALSE} and generates a {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE} error. Call {@link #glfwVulkanSupported VulkanSupported} to check whether Vulkan is
     * available and {@link #glfwGetRequiredInstanceExtensions GetRequiredInstanceExtensions} to check what instance extensions are required.</p>
     * 
     * <p>Possible errors include {@link GLFW#GLFW_NOT_INITIALIZED NOT_INITIALIZED}, {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE} and {@link GLFW#GLFW_PLATFORM_ERROR PLATFORM_ERROR}.</p>
     * 
     * <p>This function may be called from any thread. For synchronization details of Vulkan objects, see the Vulkan specification.</p>
     *
     * @param instance    the instance that the physical device belongs to
     * @param device      the physical device that the queue family belongs to
     * @param queuefamily the index of the queue family to query
     *
     * @return {@link GLFW#GLFW_TRUE TRUE} if the queue family supports presentation, or {@link GLFW#GLFW_FALSE FALSE} otherwise
     *
     * @since version 3.2
     */
    @NativeType("int")
    public static boolean glfwGetPhysicalDevicePresentationSupport(VkInstance instance, VkPhysicalDevice device, @NativeType("uint32_t") int queuefamily) {
        return true;
    }

    // --- [ glfwCreateWindowSurface ] ---

    /**
     * Creates a Vulkan surface for the specified window.
     * 
     * <p>If the Vulkan loader was not found at initialization, this function returns {@link VK10#VK_ERROR_INITIALIZATION_FAILED} and generates a {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE} error.
     * Call {@link #glfwVulkanSupported VulkanSupported} to check whether the Vulkan loader was found.</p>
     * 
     * <p>If the required window surface creation instance extensions are not available or if the specified instance was not created with these extensions
     * enabled, this function returns {@link VK10#VK_ERROR_EXTENSION_NOT_PRESENT} and generates a {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE} error. Call {@link #glfwGetRequiredInstanceExtensions GetRequiredInstanceExtensions} to
     * check what instance extensions are required.</p>
     * 
     * <p>The window surface cannot be shared with another API so the window must have been created with the client api hint set to {@link GLFW#GLFW_NO_API NO_API} otherwise it
     * generates a {@link GLFW#GLFW_INVALID_VALUE INVALID_VALUE} error and returns {@link KHRSurface#VK_ERROR_NATIVE_WINDOW_IN_USE_KHR}.</p>
     * 
     * <p>The window surface must be destroyed before the specified Vulkan instance. It is the responsibility of the caller to destroy the window surface. GLFW
     * does not destroy it for you. Call {@link KHRSurface#vkDestroySurfaceKHR} to destroy the surface.</p>
     * 
     * <p>Possible errors include {@link GLFW#GLFW_NOT_INITIALIZED NOT_INITIALIZED}, {@link GLFW#GLFW_API_UNAVAILABLE API_UNAVAILABLE}, {@link GLFW#GLFW_PLATFORM_ERROR PLATFORM_ERROR} and {@link GLFW#GLFW_INVALID_VALUE INVALID_VALUE}.</p>
     * 
     * <p>If an error occurs before the creation call is made, GLFW returns the Vulkan error code most appropriate for the error. Appropriate use of
     * {@link #glfwVulkanSupported VulkanSupported} and {@link #glfwGetRequiredInstanceExtensions GetRequiredInstanceExtensions} should eliminate almost all occurrences of these errors.</p>
     * 
     * <p>This function may be called from any thread. For synchronization details of Vulkan objects, see the Vulkan specification.</p>
     *
     * @param instance  the Vulkan instance to create the surface in
     * @param window    the window to create the surface for
     * @param allocator the allocator to use, or {@code NULL} to use the default allocator.
     * @param surface   where to store the handle of the surface. This is set to {@link VK10#VK_NULL_HANDLE} if an error occurred.
     *
     * @return {@link VK10#VK_SUCCESS} if successful, or a Vulkan error code if an error occurred
     *
     * @since version 3.2
     */
    @NativeType("VkResult")
    public static int glfwCreateWindowSurface(VkInstance instance, @NativeType("GLFWwindow *") long window, @Nullable @NativeType("VkAllocationCallbacks const *") VkAllocationCallbacks allocator, @NativeType("VkSurfaceKHR *") LongBuffer surface) {
        if (CHECKS) {
            check(surface, 1);
        }
        /* Since LWJGL didn't generate Android stuff, we have to lend Metal's one to call */
        if (Platform.get() == Platform.MACOSX) {
            VkMetalSurfaceCreateInfoEXT pCreateInfo = VkMetalSurfaceCreateInfoEXT
                .calloc()
                .sType(VK_STRUCTURE_TYPE_METAL_SURFACE_CREATE_INFO_EXT)
                .pLayer(PointerBuffer.create(window, 1));
            return vkCreateMetalSurfaceEXT(instance, pCreateInfo, null, surface);
        } else {
            VkAndroidSurfaceCreateInfoKHR pCreateInfo = VkAndroidSurfaceCreateInfoKHR
                .calloc()
                .sType(VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR)
                .pWindow(PointerBuffer.create(window, 1));
            return vkCreateAndroidSurfaceKHR(instance, pCreateInfo, null, surface);
        }
    }

    /** Array version of: {@link #glfwCreateWindowSurface CreateWindowSurface} */
    @NativeType("VkResult")
    public static int glfwCreateWindowSurface(VkInstance instance, @NativeType("GLFWwindow *") long window, @Nullable @NativeType("VkAllocationCallbacks const *") VkAllocationCallbacks allocator, @NativeType("VkSurfaceKHR *") long[] surface) {
        MemoryStack stack = stackGet();
        LongBuffer pSurface = stack.mallocLong(1);
        int result = glfwCreateWindowSurface(instance, window, allocator, pSurface);
        surface[0] = pSurface.get(0);
        return result;
    }

}
